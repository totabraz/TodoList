package totabraz.com.todolist.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import totabraz.com.todolist.R;
import totabraz.com.todolist.adapters.TodoAdapter;
import totabraz.com.todolist.model.Todo;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Todo> todos;
    private FirebaseAuth mAuth;
    private final String TAG = "MainActivity";
    private TextView tvNothing;
    private ProgressBar progressBar;
    private TextInputEditText edtTodo;
    private RecyclerView rvTodoList;
    private TodoAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mAuth = FirebaseAuth.getInstance();
        this.tvNothing = findViewById(R.id.tvNothingToDo);
        this.progressBar = findViewById(R.id.progressBar);
        this.edtTodo = findViewById(R.id.edtAddTodo);
        Button btnAdd = findViewById(R.id.btnAddTodo);
        this.rvTodoList = findViewById(R.id.rvTodoList);

        progressBar.setVisibility(View.VISIBLE);
        tvNothing.setVisibility(View.GONE);
        rvTodoList.setVisibility(View.GONE);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todo = edtTodo.getText().toString();
                if (!todo.equals("")) {
                    addTodo(todo);
                    edtTodo.setText("");
                    hideKeyboard();
                }
            }
        });
        this.getMyTodos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void addTodo(String todoTxt) {
        Todo todo = new Todo();
        todo.setMsgm(todoTxt);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        todo.setId(sdf.format(cal.getTime()));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
        mDatabase.child(todo.getId()).setValue(todo);
    }

    private void getMyTodos() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
        ValueEventListener todoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todos = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Todo todo = objSnapshot.getValue(Todo.class);
                    todos.add(todo);
                }
                if (todos.size() < 1) {
                    tvNothing.setVisibility(View.VISIBLE);
                    rvTodoList.setVisibility(View.GONE);
                } else {
                    tvNothing.setVisibility(View.GONE);
                    rvTodoList.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mAdapter = new TodoAdapter(getApplicationContext(), todos);
                    rvTodoList.setLayoutManager(mLayoutManager);
                    rvTodoList.setItemAnimator(new DefaultItemAnimator());
                    rvTodoList.setAdapter(mAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        };
        mDatabase.addValueEventListener(todoListener);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtTodo.getWindowToken(), 0);
    }

}
