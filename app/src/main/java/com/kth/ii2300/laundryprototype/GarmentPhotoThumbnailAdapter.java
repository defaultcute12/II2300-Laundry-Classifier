package com.kth.ii2300.laundryprototype;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Elvar on 30.12.2017.
 */

public class GarmentPhotoThumbnailAdapter extends ArrayAdapter {
    Context context;
    int layoutResourceId;
    ArrayList<Uri> imageURIs = null;

    public GarmentPhotoThumbnailAdapter(Context context, int layoutResourceId, ArrayList<Uri> imageURIs) {
        super(context, layoutResourceId, imageURIs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.imageURIs = imageURIs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View imgView = convertView;
        GarmentPhotoThumbnailAdapter.ImageHolder holder = null;

        if(imgView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            imgView = inflater.inflate(layoutResourceId, parent, false);

            holder = new GarmentPhotoThumbnailAdapter.ImageHolder();
            holder.imgView = (ImageView)imgView.findViewById(R.id.thumbnailImageView);

            imgView.setTag(holder);
        }
        else
        {
            holder = (GarmentPhotoThumbnailAdapter.ImageHolder)imgView.getTag();
        }

        final Uri imageUri = imageURIs.get(position);
        holder.imgView.setImageURI(imageUri);

        return imgView;
    }

    static class ImageHolder
    {
        ImageView imgView;
    }


}

