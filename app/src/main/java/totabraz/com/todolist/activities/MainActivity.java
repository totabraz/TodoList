package totabraz.com.todolist.activities;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
        getSupportActionBar().hide();
        this.mAuth = FirebaseAuth.getInstance();
        this.tvNothing = findViewById(R.id.tvNothingToDo);
        this.progressBar = findViewById(R.id.progressBar);
        this.edtTodo = findViewById(R.id.edtAddTodo);
        this.rvTodoList = findViewById(R.id.rvTodoList);

        progressBar.setVisibility(View.VISIBLE);
        tvNothing.setVisibility(View.GONE);
        rvTodoList.setVisibility(View.GONE);

        edtTodo.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addNote();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        Button btnAdd = findViewById(R.id.btnAddTodo);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });
        if(isNetworkAvailable()) this.getMyTodos();
    }

    private void addNote() {
        if(isNetworkAvailable()){
            String todo = edtTodo.getText().toString();
            if (!todo.equals("")) {
                addTodo(todo);
                edtTodo.setText("");
                hideKeyboard();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }
}
