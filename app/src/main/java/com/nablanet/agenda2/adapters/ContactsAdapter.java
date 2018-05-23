package com.nablanet.agenda2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nablanet.agenda2.R;
import com.nablanet.agenda2.adapters.ContactsAdapter.ItemHolder;
import com.nablanet.agenda2.pojos.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ItemHolder> {

    private AppCompatActivity activity;
    private List<User> users;

    public ContactsAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        User user = users.get(position);
        holder.bindItem(user);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.contact_item;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(User user){
        for (User contact : users)
            if (contact.uid.equals(user.uid))
                return users.indexOf(contact);
        return -1;
    }

    public void addContact(User user){
        users.add(0, user);
        notifyItemInserted(0);
    }

    public void updateLista(List<User> contacts){
        this.users = (contacts == null) ? new ArrayList<User>() : contacts;
        notifyDataSetChanged();
    }

    public List<User> getUsers(){
        return users;
    }

    class ItemHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CardView cardView;
        ImageView imageView;
        TextView uName, cName, cPhone;

        User user;

        ItemHolder(View view) {

            super(view);

            cardView = view.findViewById(R.id.card_view_compra);
            imageView = view.findViewById(R.id.contact_image);

            cName = view.findViewById(R.id.c_name);
            cPhone = view.findViewById(R.id.c_phone);
            uName = view.findViewById(R.id.u_name);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        void bindItem(User user) {

            this.user = user;

            cName.setText(user.contactValues.name);
            cPhone.setText(user.contactValues.phone);
            uName.setText(String.format("~ %s", user.name));


        }

        @Override
        public void onClick(View v) {
            //activity.onItemClick(compra, producto);
        }

        @Override
        public boolean onLongClick(View v) {
            //activity.launchDialogOptions(compra, producto);
            return true;
        }
    }

}
