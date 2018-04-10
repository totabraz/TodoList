package totabraz.com.todolist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import totabraz.com.todolist.R;
import totabraz.com.todolist.model.Todo;

/**
 * Created by totabraz on 09/04/18.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private ArrayList<Todo> todos;
    private Context context;

    public TodoAdapter(Context context, ArrayList<Todo> todos) {
        this.context = context;
        this.todos = todos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(itemView, parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == todos.size() - 1) {
            holder.viewBorderBottom.setVisibility(View.GONE);
        }
        holder.tvTodo.setText(todos.get(position).getMsgm());
        holder.tvTodo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseAuth mAuth = FirebaseAuth.getInstance();
                 DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
                 mDatabase.child(todos.get(position).getId()).removeValue();
                 // mDatabase.childe(todos.get(position).getId()).setValue(null);

             }
}
        );
    }


    @Override
    public int getItemCount() {
        return todos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewBorderBottom;
        TextView tvTodo;

        public ViewHolder(View holder, ViewGroup parent) {
            super(holder);
            viewBorderBottom = holder.findViewById(R.id.borderBottom);
            tvTodo = holder.findViewById(R.id.tvNote);


        }
    }

}
