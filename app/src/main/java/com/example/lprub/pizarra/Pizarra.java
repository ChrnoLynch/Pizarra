package com.example.lprub.pizarra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pizarra extends AppCompatActivity implements ColorPicker.OnColorChangedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Vista vista;
    private Bitmap bitmap;
    public static final int REQUEST_IMAGE_GET = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizarra);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.mipmap.arrowright,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.arrowright);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.arrowleft);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.arrowright);
        vista = (Vista)

            findViewById(R.id.lienzo);

            ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOldCenterColor(picker.getColor());
        picker.setOnColorChangedListener(this);
        picker.setShowOldCenterColor(false);
        opacityBar.setOnOpacityChangedListener(new OpacityBar.OnOpacityChangedListener() {
            @Override
            public void onOpacityChanged(int opacity) {
                onColorChanged(vista.getColor());
            }
        });
    }



    @Override
    	protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        	}

            	@Override
    	public void onConfigurationChanged(Configuration newConfig) {
        	    super.onConfigurationChanged(newConfig);
                    drawerToggle.onConfigurationChanged(newConfig);
        	}
         	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {

        	    // I think there's a bug in mToggle.onOptionsItemSelected, because it always returns false.
            // The item id testing is a fix.
            if (drawerToggle.onOptionsItemSelected(item) || item.getItemId() == android.R.id.home) {
            	        return true;
            }
        	    return super.onOptionsItemSelected(item);
        }


    @Override
    public void onColorChanged(int color) {
        vista.setColor(color);
    }

    public void recta(View v){
        vista.setForma("recta");
    }

    public void circulo(View v){
        vista.setForma("circulo");
    }

    public void cuadrado(View v){
        vista.setForma("cuadrado");
    }
    public void bigote(View v){
        vista.setForma("bigote");
    }
    public void poligonal(View v){
        vista.setForma("poligonal");
    }
    public void papelera(View v){
        vista.borraPantalla();
    }
    public void goma(View v){
        vista.setBorrar();
    }

    public void cargarFotoGaleria(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GET) {
            Uri uri = data.getData();
            if (uri != null) { //Obtiene la Uri de la imagen
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vista.setMapaDeBits(bitmap);
            }
        }
    }

    public void guardar(View v) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("¿Desea guardar los cambios?");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bitmap  bm = Bitmap.createBitmap( vista.getWidth(), vista.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                vista.draw(canvas);
                guardarCambio(bm);
                Toast.makeText(getApplicationContext(), "La foto ha sido guardada en myAppDir/imagen",
                        Toast.LENGTH_LONG).show();
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        alert.show();
    }

    private boolean guardarCambio(Bitmap imageData) {
        //Obtener ruta de la memoria externa
        String ruta="/myAppDir/imagen/";
        String iconsStoragePath = Environment.getExternalStorageDirectory() + ruta;
        File sdIconStorageDir = new File(iconsStoragePath);
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-mm-yy");
        String fecha=simpleDateFormat.format(date);
        String filename=" "+fecha+".JPEG";

        //Crea el directorio si este no existe
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {}

        return true;
    }



}
