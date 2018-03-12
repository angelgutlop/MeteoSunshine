package com.example.angel.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetallesTiempo_Activity extends AppCompatActivity {
    TextView tvPrevision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tiempo);

        tvPrevision = findViewById(R.id.tv_detalle_pronostico_tiempo);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String prevision = intent.getStringExtra(Intent.EXTRA_TEXT);
            tvPrevision.setText(prevision);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalles, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.accion_compartir: {

                String texto = tvPrevision.getText().toString();
                String mimeType = "text/Plain";
                ShareCompat.IntentBuilder shareIntent = ShareCompat.IntentBuilder.from(this);
                shareIntent.setChooserTitle("Compartir mediante");
                shareIntent.setType(mimeType).setText(texto);

                Intent intent = shareIntent.getIntent();

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
