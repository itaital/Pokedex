package itaital100.gmail.com.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {
    public static class PokedexViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout containerView;
        public TextView textView;

        PokedexViewHolder(View view){
            super(view);
            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(view.getContext(), PokemonActivity.class);
                    intent.putExtra("url", current.getUrl());
                    intent.putExtra("number", current.getNumber());

                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new PokemonFilter();
    }

    private class PokemonFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // search pokemon!
            List<Pokemon> filteredPokemon = new ArrayList<>();
            FilterResults results = new FilterResults();
            for(int i=0; i<pokemon.size();i++){
                if(pokemon.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                    filteredPokemon.add(pokemon.get(i));
                }
            }
            results.values = filteredPokemon;
            results.count = filteredPokemon.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }

    private List<Pokemon> pokemon = new ArrayList<>();
    private List<Pokemon> filtered  = new ArrayList<>();
    private RequestQueue requestQueue; // need contex but he dosent have we take with constructor


    PokedexAdapter(Context context){
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
        filtered = pokemon;
    }

    public void loadPokemon(){ // using volley liabery that we install we take data from the API
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { // automaticly create, out acion when data finish loading
                try {
                    JSONArray results = response.getJSONArray("results"); // take the value from the key
                    for(int i=0; i< results.length(); i++){
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemon.add(new Pokemon(
                                name.substring(0, 1).toUpperCase() + name.substring(1),
                                result.getString("url"),i+1

                        ));
                    }
                    notifyDataSetChanged(); // inform the recycler that we have new data
                } catch (JSONException e) {
                    Log.e("itai", "Json error");
                }
            }
        }, new Response.ErrorListener() { // last parameter that don't have important (only if site dosent exist)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("itai","pokemon list erro");
            }
        });
        requestQueue.add(request); // basically what make the request
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // hold the xml, convert it to java object
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row, parent, false);
        return  new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position){
        Pokemon current = filtered.get(position); //pokemon
        holder.textView.setText(current.getName());
        holder.containerView.setTag(current); // give access to the pokemon list to viewholder
    }

    @Override
    public int getItemCount() { // tell the adapter the size of my list
        return filtered.size(); // pokemon
    }



}
