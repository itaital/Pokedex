package itaital100.gmail.com.pokedex;
// second screen that show pokemons and the ID taking with Intent from PokedexAdapter

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private  TextView numberTextView;
    private String url;
    private TextView type1TextView;
    private TextView type2TextView;
    private RequestQueue requestQueue,requestQueue2;
    private String isCatch = "false";
    private Button btn_catch;
    SharedPreferences prefs;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView picture;
    private TextView description;
    private int poke_nember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        poke_nember = getIntent().getIntExtra("number",0);
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        btn_catch = findViewById(R.id.btn_catch);
        picture = findViewById(R.id.ImageViewPicture);
        description = findViewById(R.id.pokemon_desc);
        requestQueue2 = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getApplicationContext().getSharedPreferences("PokemonPull", 0);
        editor = sharedPreferences.edit();

        btn_catch.setEnabled(false);
        load();
        loadDesc();
        btn_catch.setEnabled(true);




    }



    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("itai", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView!
            picture.setImageBitmap(bitmap);
        }
    }


    public void load(){
        type1TextView.setText("");
        type2TextView.setText("");


        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { // automaticly create, out acion when data finish loading
                try {
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));
                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        if (slot ==1){
                            type1TextView.setText(type);
                        }
                        else if (slot ==2){
                            type2TextView.setText(type);
                        }
                    }
                    JSONObject SpritesEntries = response.getJSONObject("sprites");
                    String sprites = SpritesEntries.getString("front_shiny");
                    if(sprites == null)
                        sprites = SpritesEntries.getString("front_default");
                    new DownloadSpriteTask().execute(sprites);

                    if (sharedPreferences.getBoolean(response.getString("name"), false)) {
                        catchPokemon();
                    } else {
                        releasePokemon();
                    }


                } catch (JSONException e) {
                    Log.e("itai", "Pokemon jason error");
                }
            }
        }, new Response.ErrorListener() { // last parameter that don't have important (only if site dosent exist)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("itai","pokemon details erro");
            }
        });

        requestQueue.add(request);

    }

    public void loadDesc() {
        ///////////////////////////////////////////////////////////////////////////////////////
        description.setText("");
        String url2 = "https://pokeapi.co/api/v2/pokemon-species/" + poke_nember;
        final JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response2) { // automaticly create, out acion when data finish loading
                try {
                    JSONArray typeEntries = response2.getJSONArray("flavor_text_entries");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        String detail = typeEntry.getString("flavor_text");
                        String language = typeEntry.getJSONObject("language").getString("name");
                        if (language.equals("en")){
                            description.setText(detail);
                            break;
                        }
                    }

                } catch (JSONException e) {
                    Log.e("itai", "Pokemon jason error");
                }
            }
        }, new Response.ErrorListener() { // last parameter that don't have important (only if site dosent exist)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("itai","pokemon details erro");
            }
        });

        requestQueue2.add(request2);

    }


    public void toggleCatch(View view) {
        // gotta catch 'em all!

        if(isCatch.equals("false"))
        {
            catchPokemon();
        }
        else{
            releasePokemon();

        }
    }

    public void catchPokemon(){
        Toast toast;
        String msg1 = nameTextView.getText() + " was Caught!";
        String msg2 = nameTextView.getText() + " was Release!";

        isCatch = "true";
        btn_catch.setText("Release");
        if(btn_catch.isPressed()) {
            toast = Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_SHORT);
            toast.show();
        }
        //prefs.edit().putString(nameTextView.getText().toString(), "true").apply(); //init
        //String a = getPreferences(Context.MODE_PRIVATE).getString(nameTextView.getText().toString(), "");
        //Log.i("saved string Itai9", a);
        editor.putBoolean(nameTextView.getText().toString(), true);
        editor.commit();
    }

    public void releasePokemon(){
        Toast toast;
        String msg1 = nameTextView.getText() + " was Caught!";
        String msg2 = nameTextView.getText() + " was Release!";
        isCatch = "false";
        btn_catch.setText("Catch");
        if(btn_catch.isPressed()){
            toast=Toast.makeText(getApplicationContext(),msg2,Toast.LENGTH_SHORT);
            toast.show();
        }

        //prefs.edit().putString(nameTextView.getText().toString(), "false").apply(); //init
        editor.remove(nameTextView.getText().toString());
        editor.commit();
    }


}




