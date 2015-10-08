package com.tae.twitter.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tae.twitter.R;
import com.tae.twitter.gson.Tweet;

import java.util.ArrayList;

/**
 * Adapter to show tweets
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class TwitterAdapter  extends ArrayAdapter<Tweet> {
    LayoutInflater inflater;

    public TwitterAdapter(Activity context, ArrayList<Tweet> tweets) {
        super(context, 0, tweets);
        inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Tweet tweet;

        if(convertView == null || !(convertView.getTag() instanceof  ViewHolder)) {
            convertView = inflater.inflate(R.layout.tweet_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userPic = (ImageView)convertView.findViewById(R.id.userPic);
            viewHolder.userNameTwitterHandler = (TextView) convertView.findViewById(R.id.userNameTwitterHandler);
            viewHolder.publishedDate = (TextView) convertView.findViewById(R.id.publishedDate);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        tweet = getItem(position);

        // Item Info
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.userPic);
        viewHolder.userNameTwitterHandler.setText(tweet.getUser().getName() + " @" + tweet.getUser().getScreenName());
        viewHolder.publishedDate.setText(tweet.getCreatedAt());
        viewHolder.text.setText(Html.fromHtml(tweet.getText()));

        return convertView;
    }

    static class ViewHolder {
        ImageView userPic;
        TextView userNameTwitterHandler;
        TextView publishedDate;
        TextView text;
    }
}
