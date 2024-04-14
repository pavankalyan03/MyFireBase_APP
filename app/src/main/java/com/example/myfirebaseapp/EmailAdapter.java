package com.example.myfirebaseapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<String> emailList;

    public EmailAdapter(List<String> emailList) {
        this.emailList = emailList;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_element, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        String email = emailList.get(position);
        holder.bind(email);
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    class EmailViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewEmail;
        private Button buttondelete, buttonresetpwd;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmail = itemView.findViewById(R.id.user);
            buttondelete = itemView.findViewById(R.id.Deleteu);
            buttonresetpwd = itemView.findViewById(R.id.Resettu);
        }

        public void bind(String email) {
            textViewEmail.setText(email);

            buttondelete.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    showAlertDialog("delete");
                }
            });

            buttonresetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog("reset");
                }
            });
        }

        private void showAlertDialog(String action) {

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Confirm aa!");
            builder.setMessage("Nizam gane ee pani chestunnava?");

            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (action.equals("delete")){
                        String email = textViewEmail.getText().toString();
                        DBhandler dbHandler = new DBhandler(itemView.getContext());
                        dbHandler.deleteUser(email);
                        emailList.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }

                    else{
                        String email = textViewEmail.getText().toString();
                        DBhandler dbHandler = new DBhandler(itemView.getContext());
                        dbHandler.resetPassword(email);
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }


    }
}
