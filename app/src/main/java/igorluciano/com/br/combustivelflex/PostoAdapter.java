package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostoAdapter extends RecyclerView.Adapter<PostoAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onItemClick(Posto posto);
    }

    private OnItemClickListener listener;
    private final List<Posto> postos = new ArrayList<>();

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    private final DecimalFormat precoFormat = new DecimalFormat(
            "#,##0.00",
            DecimalFormatSymbols.getInstance(new Locale("pt", "BR"))
    );
    private final DecimalFormat distKmFormat = new DecimalFormat(
            "0.0",
            DecimalFormatSymbols.getInstance(new Locale("pt", "BR"))
    );

    void setPostos(List<Posto> lista) {
        postos.clear();
        postos.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posto posto = postos.get(position);
        Context ctx = holder.itemView.getContext();

        holder.nome.setText(posto.getNome() != null ? posto.getNome() : "");
        holder.distancia.setText(formatDistancia(ctx, posto.getDistanciaMetros()));
        holder.preco.setText(formatPreco(ctx, posto.getPrecoGasolinaComum()));
        holder.precoEtanol.setText(formatPreco(ctx, posto.getPrecoEtanol()));

        bindBrand(holder, posto.getBandeira());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(posto);
        });
    }

    private void bindBrand(ViewHolder holder, String bandeira) {
        @DrawableRes int logoRes = logoRes(bandeira);
        if (logoRes != 0) {
            holder.brandContainer.setBackground(null);
            holder.brandInitial.setVisibility(View.GONE);
            holder.brandLogo.setImageResource(logoRes);
            holder.brandLogo.setVisibility(View.VISIBLE);
        } else {
            holder.brandLogo.setVisibility(View.GONE);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(0xFF98A2B3);
            holder.brandContainer.setBackground(circle);
            holder.brandInitial.setText("?");
            holder.brandInitial.setVisibility(View.VISIBLE);
        }
    }

    @DrawableRes
    private int logoRes(String bandeira) {
        if (bandeira == null) return 0;
        switch (bandeira.toLowerCase(Locale.ROOT)) {
            case "ipiranga": return R.drawable.ic_brand_ipiranga;
            case "shell":    return R.drawable.ic_brand_shell;
            case "ale":      return R.drawable.ic_brand_ale;
            case "vibra":    return R.drawable.ic_brand_vibra;
            default:         return 0;
        }
    }

    @Override
    public int getItemCount() {
        return postos.size();
    }

    private String formatDistancia(Context ctx, float metros) {
        if (metros < 1000) {
            return ctx.getString(R.string.stations_distance_meters, (int) metros);
        }
        return ctx.getString(R.string.stations_distance_km, distKmFormat.format(metros / 1000.0));
    }

    private String formatPreco(Context ctx, double preco) {
        if (preco <= 0) return "—";
        return ctx.getString(R.string.stations_price_format, precoFormat.format(preco));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View brandContainer;
        final TextView brandInitial;
        final ImageView brandLogo;
        final TextView nome;
        final TextView distancia;
        final TextView preco;
        final TextView precoEtanol;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandContainer = itemView.findViewById(R.id.item_brand_container);
            brandInitial = itemView.findViewById(R.id.item_brand_initial);
            brandLogo = itemView.findViewById(R.id.item_brand_logo);
            nome = itemView.findViewById(R.id.item_posto_nome);
            distancia = itemView.findViewById(R.id.item_posto_distancia);
            preco = itemView.findViewById(R.id.item_posto_preco);
            precoEtanol = itemView.findViewById(R.id.item_posto_preco_etanol);
        }
    }
}
