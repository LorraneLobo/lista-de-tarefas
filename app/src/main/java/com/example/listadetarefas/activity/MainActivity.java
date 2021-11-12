package com.example.listadetarefas.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.listadetarefas.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadetarefas.adapter.TarefaAdapter;
import com.example.listadetarefas.databinding.ActivityMainBinding;
import com.example.listadetarefas.helper.DbHelper;
import com.example.listadetarefas.helper.RecyclerItemClickListener;
import com.example.listadetarefas.helper.TarefaDAO;
import com.example.listadetarefas.model.Tarefa;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private RecyclerView recyclerListaTarefas;
    private TarefaAdapter tarefaAdapter;
    private Tarefa tarefaSelecionada;
    private List<Tarefa> listaTarefas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //Configurar recycler
        recyclerListaTarefas = binding.content.recyclerListaTarefas;

        //Adicionar evento de clique
        recyclerListaTarefas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerListaTarefas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                //Recuperar tarefa para edicao
                                tarefaSelecionada = listaTarefas.get(position);

                                //Envia tarefa para tela adicionar tarefa
                                Intent intent = new Intent(MainActivity.this, AdicionarTarefaActivity.class);
                                intent.putExtra("tarefaSelecionada", tarefaSelecionada);

                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                //Recuperar tarefa para deletar
                                tarefaSelecionada = listaTarefas.get(position);

                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                                //Configura título e mensagem
                                dialog.setTitle("Confirmar exclusão");
                                dialog.setMessage("Excluir a tarefa: " + tarefaSelecionada.getNomeTarefa() + " ?");

                                dialog.setPositiveButton("Sim", (dialog1, which) -> {


                                    TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                                    if (tarefaDAO.deletar(tarefaSelecionada)){

                                        carregarListaTarefas();
                                        Toast.makeText(getApplicationContext(), "Tarefa excluída",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Erro ao excluir tarefa",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.setNegativeButton("Não", null);

                                //Exibir dialog
                                dialog.create();
                                dialog.show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AdicionarTarefaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void carregarListaTarefas(){

        //Listar tarefas
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        listaTarefas = tarefaDAO.listar();
        /*
            Exibe lista de tarefas no Recyclerview
        */
        //Configurar um adapter
        tarefaAdapter = new TarefaAdapter(listaTarefas);

        //Configurar Recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerListaTarefas.setLayoutManager(layoutManager);
        recyclerListaTarefas.setHasFixedSize(true);
        recyclerListaTarefas.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerListaTarefas.setAdapter(tarefaAdapter);
    }

    @Override
    protected void onStart() {
        carregarListaTarefas();
        super.onStart();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_adicionar_tarefa, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.itemSalvar:
//                //Executa ação para o item salvar
//                TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
//                //tarefaDAO.salvar();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}