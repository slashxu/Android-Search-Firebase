package com.soliwork.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soliwork.search.modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dataRef;

    EditText editNome, editEmail;
    ListView listV_dados;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    Pessoa pessoaSelecionada;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        listV_dados = findViewById(R.id.listV_dados);

        inicializeDatabase();

        eventoDatabase();
        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) parent.getItemAtPosition(position);
                editNome.setText(pessoaSelecionada.getNome());
                editEmail.setText(pessoaSelecionada.getEmail());
            }
        });
    }

    private void eventoDatabase() {
        dataRef.child("Pessoas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoa.clear();

                for (DataSnapshot objSnap : dataSnapshot.getChildren()) {
                    Pessoa prod = objSnap.getValue(Pessoa.class);
                    listPessoa.add(prod);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this, android.R.layout.simple_list_item_1, listPessoa);

                listV_dados.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Inicializa o banco de dados (Firebase)
    private void inicializeDatabase() {
        FirebaseApp.initializeApp(MainActivity.this);
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        dataRef = database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_novo) {
            Pessoa prod = new Pessoa();
            prod.setUid(UUID.randomUUID().toString());
            prod.setNome(editNome.getText().toString());
            prod.setEmail(editEmail.getText().toString());

            // Cria a tabela Pessoas e seta chave primaria no id
            dataRef.child("Pessoas").child(prod.getUid()).setValue(prod);
            limparCampos();
        } else if (id == R.id.menu_atualizar) {
            if (pessoaSelecionada != null){
                atualizaPessoa();
            }else{
                alert("Selecione um registro para atualizar");
            }
        } else if (id == R.id.menu_deletar) {
            if (pessoaSelecionada != null){
                deletaPessoa();
            }else{
                alert("Selecione um registro para deletar");
            }
        }else if (id == R.id.menu_pesquisa) {
            Intent i = new Intent(MainActivity.this,Pesquisa.class);
            startActivity(i);
        }

        return true;
    }

    private void deletaPessoa() {
        Pessoa prod = new Pessoa();
        prod.setUid(pessoaSelecionada.getUid());
        dataRef.child("Pessoas").child(prod.getUid()).removeValue();
        limparCampos();
    }

    private void alert(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    private void atualizaPessoa() {
        Pessoa prod = new Pessoa();
        prod.setUid(pessoaSelecionada.getUid());
        prod.setNome(editNome.getText().toString().trim());
        prod.setEmail(editEmail.getText().toString().trim());
        // Cria a tabela Produtos e seta chave primaria no id
        dataRef.child("Pessoas").child(prod.getUid()).setValue(prod);
        limparCampos();
    }

    private void limparCampos() {
        editNome.setText("");
        editEmail.setText("");
    }
}
