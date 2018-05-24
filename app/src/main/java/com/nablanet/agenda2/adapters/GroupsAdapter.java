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
import com.nablanet.agenda2.adapters.GroupsAdapter.ItemHolder;
import com.nablanet.agenda2.pojos.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<ItemHolder> {

    private AppCompatActivity activity;
    private List<Group> groups;

    public GroupsAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.groups = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Group group = groups.get(position);
        holder.bindItem(group);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_contact;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(Group group){
        for (Group groupOnList : groups)
            if (groupOnList.getGid().equals(group.getGid()))
                return groups.indexOf(group);
        return -1;
    }

    public void addGroup(Group group){
        groups.add(0, group);
        notifyItemInserted(0);
    }

    public void updateLista(List<Group> groups){
        this.groups = (groups == null) ? new ArrayList<Group>() : groups;
        notifyDataSetChanged();
    }

    public List<Group> getGroups(){
        return groups;
    }

    class ItemHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CardView cardView;
        ImageView imageView;
        TextView groupName, groupComment, groupOwner;

        Group group;

        ItemHolder(View view) {

            super(view);

            cardView = view.findViewById(R.id.card_view_compra);
            imageView = view.findViewById(R.id.contact_image);

            groupName = view.findViewById(R.id.group_name);
            groupComment = view.findViewById(R.id.group_comment);
            groupOwner = view.findViewById(R.id.group_owner);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        void bindItem(Group group) {

            this.group = group;

            groupName.setText(group.getName());
            groupComment.setText(group.getComment());
            groupOwner.setText(group.getOwnerUid());


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
