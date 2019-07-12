package com.soliwork.search;

import android.app.DownloadManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.soliwork.search.modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;

public class Pesquisa extends AppCompatActivity {

    private EditText editPalavra;
    private ListView listVPesquisa;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);

        inicializeCompnonests();
        inicializeFirebase();
        eventoEdit();
    }

    private void eventoEdit() {
        editPalavra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra = editPalavra.getText().toString().trim();
                pesquisaPalavra(palavra);
            }
        });
    }

    // Faz a pesquisa no banco de dados
    private void pesquisaPalavra(String palavra) {
        Query query;
        if (palavra.equals("")){
            query = databaseReference.child("Pessoas").orderByChild("nome");
        }else{
            // Busca a palavra mais algo ex: Casa, Casamento, Casar...
            query = databaseReference.child("Pessoas").orderByChild("nome").endAt(palavra+"\uf8ff");
        }
        listPessoa.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnap:dataSnapshot.getChildren()){
                    Pessoa p = objSnap.getValue(Pessoa.class);
                    listPessoa.add(p);
                }

                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(Pesquisa.this, android.R.layout.simple_list_item_1,listPessoa);
                listVPesquisa.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializeFirebase() {
        FirebaseApp.initializeApp(Pesquisa.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializeCompnonests() {
        editPalavra = findViewById(R.id.editPalavra);
        listVPesquisa = findViewById(R.id.listVPesquisa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pesquisaPalavra("");
    }
}
