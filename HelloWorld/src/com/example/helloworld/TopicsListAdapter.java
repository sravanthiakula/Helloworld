package com.qaccela.salesquestions.adapters;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.qaccela.salesquestions.R;
import com.qaccela.salesquestions.db.Topicsoperations;
import com.qaccela.salesquestions.model.TopicInfo;


public class TopicsListAdapter extends BaseAdapter{

	private ArrayList<TopicInfo> topicArray;
	private LayoutInflater inflater;
	private Context context;
	private Topicsoperations topicsoperations;
	private boolean showOptions;
	public TopicsListAdapter(ArrayList<TopicInfo> topicArray, Context context, boolean showOptions) {
		this.topicArray = topicArray;
		this.context = context;
		this.showOptions = showOptions;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return topicArray.size();
	}

	@Override
	public Object getItem(int position) {
		return topicArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ListViewHolder vh;

		if(convertView == null){
			vh = new ListViewHolder();
			inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.topic_list_item, parent, false);
			vh.tv = (TextView) convertView.findViewById(R.id.topicNameText);	
			vh.expandButton = (ImageView)convertView.findViewById(R.id.expandableButton);
			vh.remove=(Button)convertView.findViewById(R.id.removeButton);
			vh.remove.setVisibility(View.GONE);
			convertView.setTag(vh);
		} else{
			vh = (ListViewHolder)convertView.getTag();
		}
		final View rowView = convertView;

		topicsoperations= new Topicsoperations(context);

		final ImageView arrowIcon = vh.expandButton;

		//TopicsModel tp = topicArray.get(position);
		final TopicInfo to = topicArray.get(position);

		vh.remove.setVisibility(View.GONE);
		vh.expandButton.setVisibility(View.GONE);

		to.setExpanded(to.isExpanded());


		if(to.isExpanded()){
			TextView rv = (TextView)((RelativeLayout)rowView).getChildAt(0);
			ViewGroup.LayoutParams params = rv.getLayoutParams();			
			params.height = LayoutParams.WRAP_CONTENT;			
			rv.setLayoutParams(params);
			rv.requestLayout();
		}else{
			TextView rv = (TextView)((RelativeLayout)rowView).getChildAt(0);
			ViewGroup.LayoutParams params = rv.getLayoutParams();			
			params.height = 45;			
			rv.setLayoutParams(params);
			rv.requestLayout();
		}



		if(showOptions) {
			//expand button
			vh.expandButton.setVisibility(View.VISIBLE);
			vh.expandButton.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					if(to.isExpanded()){
						to.toggleExpansion();
						notifyDataSetChanged();
					}else{
						handlieExpansion(position,to.isExpanded(),rowView);
					}
				}
			});
			//end expand button

			//Topic Name and overview
			String topicname = "<font  color=#000000>"+to.getTopicName()+"</font> <br><font  color=#0099CC><b>Description:</b></font><br><font color=#000000>"+to.getTopicOverview()+"</font>";
			vh.tv.setText(Html.fromHtml(topicname));
			//end Topic Name and overview

			boolean topicUserDefined = topicsoperations.isTopicUserDefined(to.getTopicId());
			if(topicUserDefined) {
				vh.remove.setVisibility(View.VISIBLE);

				vh.remove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						AlertDialog.Builder adb = new AlertDialog.Builder(context);

						adb.setTitle("Alert");

						adb.setMessage("Are you sure you want to remove");

						adb.setIcon(android.R.drawable.ic_dialog_alert);

						adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

								try{
									topicsoperations.deleteTopic(to.getTopicId());
									topicsoperations.deleteAllQuestionsbyTopicId(to.getTopicId());
									topicArray.remove(to);
									notifyDataSetChanged();
									Toast.makeText(context, "Topic removed Successfully", Toast.LENGTH_LONG).show();
								} catch(Exception e){
									e.printStackTrace();
								}
							} 	
						});

						adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});	    
						adb.show();
					}
				});

			}
		} else {
			//Topic Name
			String topicname = "<font  color=#000000>"+to.getTopicName()+"</font>";
			vh.tv.setText(Html.fromHtml(topicname));
			//end Topic Name
		}

		return convertView;
	}

	private void handlieExpansion(int pos,boolean isVisible,View v){
		TextView rv = (TextView)((RelativeLayout)v).getChildAt(0);
		ViewGroup.LayoutParams params = rv.getLayoutParams();
		for(int i=0;i<topicArray.size();i++){
			TopicInfo info = topicArray.get(i);
			if(i!=pos){
				info.setExpanded(false);
			}else{
				info.setExpanded(true);
			}
		}
		/*if(isVisible){
			params.height = 45;
		}else{
			params.height = LayoutParams.WRAP_CONTENT;
		}
		rv.setLayoutParams(params);
		rv.requestLayout();*/
		notifyDataSetChanged();
	}

	public class ListViewHolder{
		public TextView tv;
		public ImageView expandButton;	
		public Button remove;


	}
}
