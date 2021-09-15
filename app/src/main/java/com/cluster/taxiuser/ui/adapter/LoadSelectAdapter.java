package com.cluster.taxiuser.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.data.network.model.Service;

import java.util.List;

public class LoadSelectAdapter extends RecyclerView.Adapter<LoadSelectAdapter.MyViewHolder> {

    private List<Service> list;
    private Context context;


    public LoadSelectAdapter(Context context, List<Service> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_load, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linear_row;
        RadioButton rb_item;

        MyViewHolder(View view) {
            super(view);
            linear_row = (LinearLayout) view.findViewById(R.id.linear_row);
            rb_item = (RadioButton) view.findViewById(R.id.radio_item);
        }
    }
}
