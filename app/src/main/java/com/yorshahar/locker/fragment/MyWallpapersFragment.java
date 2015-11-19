package com.yorshahar.locker.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yorshahar.locker.R;

/**
 * Fragment for choosing a wallpaper from local wallpapers
 * <p/>
 * Created by yorshahar on 8/24/15.
 */
public class MyWallpapersFragment extends Fragment {
    final static String ARGS_RESOURCE_ID = "resourceId";

    private int resourceId;

    public static MyWallpapersFragment newInstance(final int resourceId) {
        MyWallpapersFragment fragment = new MyWallpapersFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_RESOURCE_ID, resourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public MyWallpapersFragment() {

    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resourceId = getArguments().getInt(ARGS_RESOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_my_wallpapers, container, false);
        View view = inflater.inflate(R.layout.wallpaper_preview_layout, container, false);
        view.setTag(resourceId);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId, options);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
