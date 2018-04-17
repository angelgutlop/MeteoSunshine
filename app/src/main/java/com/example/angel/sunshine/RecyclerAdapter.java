package com.example.angel.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angel.sunshine.data.DatosClima;
import com.example.angel.sunshine.data.PronosticoContract;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;
import com.example.angel.sunshine.utilidades.UtilidadesTiempo;

/**
 * Created by Angel on 09/03/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PrevisionItemHolder> {


    private static final String TAG = RecyclerAdapter.class.getSimpleName();
    // final eliminar esta variable y sustituirla por un cursor a la base de datos SQL.
    //private ArrayList<String> arrayTiempo = new ArrayList<>();

    private Cursor mCursor;
    private Context mContext;
    private RecyclerView.LayoutManager layoutManager;

    //Funciones para recuperar el contenido del array de tiempo


    public Long getDate(int id) {

        mCursor.moveToPosition(id);
        int ind_id = mCursor.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_FECHA);

        return mCursor.getLong(ind_id);


    }


    private OnClickListener onClickListener;


    public interface OnClickListener {
        void onItemClick(int id);
    }

    RecyclerAdapter(Context context) {
        this.mContext = context;

    }


    protected void setListener(OnClickListener Listener) {
        this.onClickListener = Listener;
    }

    // comp eliminar esta funcion y sustituirla por UPDATE FORECAST DATA
    /*protected void setForecastData(ArrayList<String> arrayTiempo) {
        this.arrayTiempo = arrayTiempo;
        this.notifyDataSetChanged();
    }*/

    protected void updateForecastData(Cursor cursor) {
        if (cursor == null) return;
        if (cursor == mCursor) return;
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public PrevisionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int idLayout = 0;
        switch (viewType) {
            case VISTA_HOY:
                idLayout = R.layout.forecast_item_hoy;
                break;

            case VISTA_FUTURA:
                idLayout = R.layout.forecast_item_list;
                break;

        }
        View view = layoutInflater.inflate(idLayout, parent, false);
        return new PrevisionItemHolder(view);


    }

    @Override
    public void onBindViewHolder(PrevisionItemHolder holder, int position) {


        mCursor.moveToPosition(position);

        int ind_date = mCursor.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_FECHA);
        long date = mCursor.getLong(ind_date);

        String fecha = UtilidadesFecha.timestamp2String(date);

        int ind_weater_id = mCursor.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_WEATHER_ID);
        int weatherId = mCursor.getInt(ind_weater_id);

        String descripcion = UtilidadesTiempo.getWeatherIdString(mContext, weatherId);


        int id_max_temp = mCursor.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_MAX_TEMP);
        double max_temp = mCursor.getDouble(id_max_temp);

        int id_min_temp = mCursor.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_MIN_TEMP);
        double min_temp = mCursor.getDouble(id_min_temp);

        //Guarda en el tag del holder el id del resgistro correspondiente para recuperar la informacion de forma sencilla cuando se produzca el evento OnClick sobre la lista

        //holder.itemView.setTag(ind_weater_id);

        DatosClima datosClima = new DatosClima(date, weatherId, max_temp, min_temp, null, null, null, null);

        String resumen = fecha + " - " + descripcion + " - " + min_temp + " a " + max_temp;

        holder.bind(datosClima);
        // Log.d(TAG, "Elemento " + position + " mostrado");

    }

    private static final int VISTA_HOY = 1;
    private static final int VISTA_FUTURA = 2;

    @Override
    public int getItemViewType(int position) {

        /*
        long tsIni = UtilidadesFecha.getStartOfDayTimestamp();
        long tsFin = UtilidadesFecha.getEndOfDayTimestamp();
        long date = this.getDate(position);
        if (date > tsIni && date < tsFin) return VISTA_HOY;
        else return VISTA_FUTURA;*/

        if(position==0) return VISTA_HOY;
        else return VISTA_FUTURA;

    }

    @Override
    public int getItemCount() {

        if (mCursor == null) return 0;

        return mCursor.getCount();
    }

    class PrevisionItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iconoClima;
        private TextView diaPronostico;
        private TextView descripcionClima;
        private TextView temperaturaMáxima;
        private TextView temperaturaMínima;

        PrevisionItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iconoClima = itemView.findViewById(R.id.ivIconoClima);
            diaPronostico = itemView.findViewById(R.id.tvDiaPronostico);
            descripcionClima = itemView.findViewById(R.id.tvDescripcionPronóstico);
            temperaturaMáxima = itemView.findViewById(R.id.tvTemperaturaMáxima);
            temperaturaMínima = itemView.findViewById(R.id.tvTemperatuaMínima);


        }

        public void bind(DatosClima prevision) {


            diaPronostico.setText(prevision.getFecha());
            descripcionClima.setText(prevision.getDescipcion(mContext));
            temperaturaMáxima.setText(prevision.getMaxTempStr(mContext));
            temperaturaMínima.setText(prevision.getMinTempStr(mContext));
            iconoClima.setImageResource(prevision.getIconoClima());

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();

            onClickListener.onItemClick(pos);
        }
    }


}
