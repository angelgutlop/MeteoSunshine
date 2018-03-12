package com.example.angel.sunshine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Angel on 09/03/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PrevisionItemHolder> {


    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    private ArrayList<String> arrayTiempo = new ArrayList<>();

    //Funciones para recuperar el contenido del array de tiempo

    public String getElement(int id) {
        if (arrayTiempo != null) {
            return arrayTiempo.get(id);
        }

        return null;
    }


    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int id);
    }

    RecyclerAdapter() {

    }


    protected void setListener(OnClickListener Listener) {
        this.onClickListener = Listener;
    }

    protected void setForecastData(ArrayList<String> arrayTiempo) {
        this.arrayTiempo = arrayTiempo;
        this.notifyDataSetChanged();
    }

    @Override
    public PrevisionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.forecast_item_list, parent, false);
        return new PrevisionItemHolder(view);


    }

    @Override
    public void onBindViewHolder(PrevisionItemHolder holder, int position) {
        holder.bind(position);
        Log.d(TAG, "Elemento " + position + " mostrado");

    }

    @Override
    public int getItemCount() {

        if (arrayTiempo == null) return 0;

        return arrayTiempo.size();
    }

    class PrevisionItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mWeatherTextView;

        PrevisionItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mWeatherTextView = itemView.findViewById(R.id.tv_informacion_meteo);

        }

        public void bind(int id) {

            mWeatherTextView.setText(arrayTiempo.get(id));
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            onClickListener.onItemClick(pos);
        }
    }
}
