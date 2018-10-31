package pe.leidy_cs.proyectointegrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;

import pe.leidy_cs.proyectointegrador.clases.Alumno;

public class ProfileActivity extends AppCompatActivity {
    EditText editTextNombre, editTextCodigo, editTextCorreo;
    String nombres, codigo, correo;
    Alumno Alumno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        this.codigo = bundle.getString("codigo");
        this.nombres = bundle.getString("nombres");
        this.correo = bundle.getString("correo");

        this.editTextCodigo = (EditText) findViewById(R.id.textCodigo);
        this.editTextNombre = (EditText) findViewById(R.id.textNombre);
        this.editTextCorreo = (EditText) findViewById(R.id.textCorreo);

        editTextCodigo.setText(codigo);
        editTextNombre.setText(nombres);
        editTextCorreo.setText(correo);
    }

    public void btn_clickEditarPersona(View view){
        Alumno = new Alumno();
        Alumno.setCodigo(editTextCodigo.getText().toString().trim());
        Alumno.setNombres(editTextNombre.getText().toString().trim());
        Alumno.setCorreo(editTextCorreo.getText().toString().trim());
        new ActualizarPersona().execute();
    }

    public void btn_clickEliminarPersona(View view){
        new EliminarPersona().execute();
    }

    private class ActualizarPersona extends AsyncTask<Void, Void, Boolean> {
        @Override
        public Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut("http://tercerintento-marco121942.c9users.io:8080/rest/alumnos/"+codigo.trim()+"/");
            httpPut.setHeader("content-type", "application/json");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("codigo", Alumno.getCodigo());
                jsonObject.put("nombres", Alumno.getNombres());
                jsonObject.put("correo", Alumno.getCorreo());

                StringEntity stringEntity = new StringEntity(jsonObject.toString());
                httpPut.setEntity(stringEntity);
                httpClient.execute(httpPut);
                return true;

            } catch (org.json.JSONException | java.io.IOException e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            String msj;
            if (result){
                msj = "Actualizado correctamente";
            } else {
                msj = "Problemas al actualizar";
            }
            Toast.makeText(ProfileActivity.this, msj, Toast.LENGTH_SHORT).show();
        }
    }

    //Eliminar Persona
    private class EliminarPersona extends AsyncTask<Void, Void, Boolean>{

        @Override
        public Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete("http://tercerintento-marco121942.c9users.io:8080/rest/alumnos/"+codigo.trim()+"/");
            httpDelete.setHeader("Content-Type", "application/json");

            try {
                httpClient.execute(httpDelete);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result){
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(ProfileActivity.this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Problema al eliminar", Toast.LENGTH_LONG).show();
            }
        }
    }
}
