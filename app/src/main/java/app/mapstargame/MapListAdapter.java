package app.mapstargame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Daniel Bucolo on 8/25/2016.
 */
public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder> {
    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private final OnItemClickListener listener;
    private LayoutInflater inflater;
    private Context context;

    public boolean prem;

    private final String[] titles;
    private final int[] icons;

    public MapListAdapter(int[] icons, String[] titles, OnItemClickListener listener, Context c,
                          boolean prem) {
        this.icons = icons;
        this.titles = titles;
        this.listener = listener;
        this.inflater = LayoutInflater.from(c);
        this.context = c;
        this.prem = prem;
    }

    @Override
    public MapListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        // create b new view
        if (type == VIEW_TYPE_ITEM) {
            View rowView = inflater.inflate(R.layout.map_list_item, parent, false);
            return new ViewHolder(rowView);
        }
        else {
            View rowView = inflater.inflate(R.layout.map_list_item_padding, parent, false);
            return new ViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        if (getItemViewType(pos)  == VIEW_TYPE_ITEM) {
            final String map = titles[pos - 1];

            holder.textView.setText(map);
            holder.imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(context.getResources(), icons[pos - 1],
                            200,
                            200));

            if (map == "Africa" || map == "Latin America") {
                holder.imageView.getLayoutParams().height = 900;
                holder.imageView.getLayoutParams().width = 450;
            }

            if (pos == 1 || pos == 2) {
                holder.priceView.setText("UNLOCKED");
            }

            if (pos == 6) {
                holder.priceView.setText("LOCKED");
            }

            if (prem) {
                holder.priceView.setText("UNLOCKED");
            }

            holder.bind(map, listener);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount() - 1) {
            return VIEW_TYPE_PADDING;
        }
        return VIEW_TYPE_ITEM;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public TextView priceView;

        public ViewHolder(View v) {
            super(v);
            this.textView = (TextView) v.findViewById(R.id.map_name);
            this.imageView = (ImageView) v.findViewById(R.id.map_icon);
            this.priceView = (TextView) v.findViewById(R.id.cost);
        }

        public void bind(final String map, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(map);
                }
            });
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is b power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public interface OnItemClickListener{
        void onItemClick(String map);
    }
}
