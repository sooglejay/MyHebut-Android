package com.myhebut.classes;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.myhebut.activity.R;
import com.squareup.picasso.Picasso;

public class NetworkImageHolderView implements Holder<String> {

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, final int position, String data) {

        Picasso.with(context).load(data)
                .error(R.mipmap.pic_banner_default).into(imageView);
    }
}
