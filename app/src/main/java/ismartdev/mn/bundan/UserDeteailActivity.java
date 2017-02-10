package ismartdev.mn.bundan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.util.Constants;

public class UserDeteailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageOne;
    private ImageView imageOneBtn;
    private ImageView imageTwo;
    private ImageView imageTwoBtn;
    private ImageView imageThree;
    private ImageView imageThreeBtn;
    private ImageView imageFour;
    private ImageView imageFourBtn;
    private ImageView imageFive;
    private ImageView imageFiveBtn;
    private ImageView imageSix;
    private ImageView imageSixBtn;
    private User user;
    private UploadTask uploadTask;
    private TextView schoolTv;
    private TextView workTv;
    private List<String> workList = new ArrayList<>();
    private List<String> schoolList = new ArrayList<>();
    private int schoolPos = -1;
    private int workPos = -1;

    private CustomAdapter customAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deteail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);


        user = (User) getIntent().getSerializableExtra("User");
        imageOne = (ImageView) findViewById(R.id.user_imagef);
        imageOneBtn = (ImageView) findViewById(R.id.user_imagef_Btn);
        imageTwo = (ImageView) findViewById(R.id.user_imagetwo);
        imageTwoBtn = (ImageView) findViewById(R.id.user_imagetwo_Btn);
        imageThree = (ImageView) findViewById(R.id.user_imagethree);
        imageThreeBtn = (ImageView) findViewById(R.id.user_imagethree_Btn);
        imageFour = (ImageView) findViewById(R.id.user_imagefour);
        imageFourBtn = (ImageView) findViewById(R.id.user_imagefour_Btn);
        imageFive = (ImageView) findViewById(R.id.user_imagefive);
        imageFiveBtn = (ImageView) findViewById(R.id.user_imagefive_Btn);
        imageSix = (ImageView) findViewById(R.id.user_imagesix);
        imageSixBtn = (ImageView) findViewById(R.id.user_imagesix_Btn);
        schoolTv = (TextView) findViewById(R.id.user_school);
        workTv = (TextView) findViewById(R.id.user_work);

        schoolTv.setText(user.education.equals("") ? "Хоосон" : user.education);
        workTv.setText(user.work.equals("") ? "Хоосон" : user.work);

        schoolTv.setOnClickListener(this);
        workTv.setOnClickListener(this);

        imageOneBtn.setOnClickListener(this);
        imageTwoBtn.setOnClickListener(this);
        imageThreeBtn.setOnClickListener(this);
        imageFourBtn.setOnClickListener(this);
        imageFiveBtn.setOnClickListener(this);
        imageSixBtn.setOnClickListener(this);

        if (user.picture.size() > 0) {
            for (int i = 0; i < user.picture.size(); i++) {
                setUploadImage(i, user.picture.get(i));
            }
        }
        getFbDatas();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    uploadImage(BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(UserDeteailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void getFbDatas() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.e("data", response.getRawResponse() + "");
                        try {
                            JSONArray edu = object.getJSONArray("education");
                            if (edu.length() > 0) {

                                for (int i = 0; i < edu.length(); i++) {
                                    JSONObject item = edu.getJSONObject(i);
                                    String str = item.getJSONObject("school").getString("name");
                                    schoolList.add(str);
                                    if (str.equals(user.education))
                                        schoolPos = i;
                                }
                            }
                            JSONArray work = object.getJSONArray("work");
                            if (work.length() > 0) {
                                for (int i = 0; i < work.length(); i++) {
                                    JSONObject item = work.getJSONObject(i);
                                    String str = "";
                                    boolean isEmployee = false;
                                    if (!item.isNull("position")) {
                                        str = item.getJSONObject("position").getString("name") + " at ";
                                        workList.add(item.getJSONObject("position").getString("name"));
                                        workList.add(item.getJSONObject("employer").getString("name"));
                                        isEmployee = true;
                                    }
                                    if (!item.isNull("employer")) {
                                        if (!str.equals(""))
                                            str = str + " " + item.getJSONObject("employer").getString("name");
                                    }
                                    if (!item.isNull("location")) {
                                        str = str + " " + item.getJSONObject("location").getString("name");
                                        workList.add(item.getJSONObject("location").getString("name"));
                                        if (!isEmployee)
                                            workList.add(item.getJSONObject("employer").getString("name"));
                                    }
                                    workList.add(str);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "education,work");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {
        if (v == imageOneBtn) {
            Crop.pickImage(UserDeteailActivity.this);
        }
        if (v == imageTwoBtn || v == imageThreeBtn || v == imageFourBtn || v == imageFiveBtn || v == imageSixBtn) {
            DeleteModel deleteModel = (DeleteModel) v.getTag();
            if (deleteModel != null) {

                deleteImage(deleteModel.index);
                v.setTag(null);
                switch (deleteModel.index) {
                    case 1:
                        imageTwoBtn.setImageResource(R.drawable.pic_add);
                        imageTwo.setImageResource(0);
                        break;
                    case 2:
                        imageThreeBtn.setImageResource(R.drawable.pic_add);
                        imageThree.setImageResource(0);
                        break;
                    case 3:
                        imageFourBtn.setImageResource(R.drawable.pic_add);
                        imageFour.setImageResource(0);
                        break;
                    case 4:
                        imageFiveBtn.setImageResource(R.drawable.pic_add);
                        imageFive.setImageResource(0);
                        break;
                    case 5:
                        imageSixBtn.setImageResource(R.drawable.pic_add);
                        imageSix.setImageResource(0);
                        break;
                }
            } else
                Crop.pickImage(UserDeteailActivity.this);
        }
        if (v == schoolTv) {
            callDialog(0);
        }
        if (v == workTv) {
            callDialog(1);
        }
    }

    class DeleteModel {
        public int index;
    }

    private void deleteImage(int index) {
        ref.child(Constants.user + "/" + getUid() + "/" + Constants.user_info + "/picture/" + index).removeValue();
    }

    private void uploadImage(Bitmap responseImage) throws IOException {
        showProgressDialog();
        final int index = user.picture.size() >= 5 ? 5 : user.picture.size();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_path));


        StorageReference userImageRef = storageRef.child("user-photos/" + getUid() + "/image" + index + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        responseImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                hideProgressDialog();
                Toast.makeText(UserDeteailActivity.this, R.string.error_url_null, Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(Constants.user + "/" + getUid() + "/" + Constants.user_info + "/picture/" + index, downloadUrl);
                if (index == 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("picture", downloadUrl);
                    editor.commit();
                    childUpdates.put(Constants.user + "-" + user.gender + "/" + getUid() + "/picture", downloadUrl);
                }
                ref.updateChildren(childUpdates);
                setUploadImage(index, downloadUrl);
                user.picture.add(downloadUrl);
                hideProgressDialog();
            }
        });


    }

    private void callDialog(final int mode) {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.list_dialog);

        ListView listView = (ListView) dialog.findViewById(R.id.dialog_list);

        final CheckedTextView noneTv = (CheckedTextView) dialog.findViewById(R.id.list_none);
        // set the adapter to fill the data in ListView
        if (mode == 0) {
            customAdapter = new CustomAdapter(getApplicationContext(), schoolList, schoolPos);
            Log.e("schoolPos", schoolPos + "-*-");
            listView.setAdapter(customAdapter);
            if (schoolPos < 0) {
                noneTv.setCheckMarkDrawable(R.drawable.ic_check);
                noneTv.setChecked(true);
            }

        } else {
            Log.e("workPos", workPos + "-*-");
            for (int i = 0; i < workList.size(); i++) {
                if (user.work.equals(workList.get(i))) {
                    workPos = i;
                }

            }
            Log.e("callDialog mode 1", workList.size() + "");
            customAdapter = new CustomAdapter(getApplicationContext(), workList, workPos);
            listView.setAdapter(customAdapter);
            if (workPos < 0) {
                noneTv.setCheckMarkDrawable(R.drawable.ic_check);
                noneTv.setChecked(true);
            }

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> childUpdates = new HashMap<>();
                String updateName = mode == 0 ? "education" : "work";
                String updateValue = mode == 0 ? schoolList.get(position) : workList.get(position);

                childUpdates.put(Constants.user + "/" + getUid() + "/" + Constants.user_info + "/" + updateName, updateValue);
                if (mode == 1)
                    childUpdates.put(Constants.user + "-" + user.gender + "/" + getUid() + "/work", updateValue);
                ref.updateChildren(childUpdates);

                customAdapter.setSelected(position);
                noneTv.setCheckMarkDrawable(null);
                noneTv.setChecked(false);
                if (mode == 0) {
                    schoolTv.setText(updateValue);
                    user.education = updateValue;
                } else {
                    workTv.setText(updateValue);
                    user.work = updateValue;
                }


            }
        });
        noneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAdapter.setSelected(-1);
                Map<String, Object> childUpdates = new HashMap<>();
                String updateName = mode == 0 ? "education" : "work";
                String updateValue = null;
                if (mode == 0) {
                    schoolTv.setText("Хоосон");
                } else {
                    workTv.setText("Хоосон");
                }
                childUpdates.put(Constants.user + "/" + getUid() + "/" + Constants.user_info + "/" + updateName, updateValue);
                if (mode == 1)
                    childUpdates.put(Constants.user + "-" + user.gender + "/" + getUid() + "/work", updateValue);

                ref.updateChildren(childUpdates);
                noneTv.setCheckMarkDrawable(R.drawable.ic_check);
                noneTv.setChecked(true);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialog.show();

    }


    class CustomAdapter extends BaseAdapter {
        public void setSelected(int pos) {
            selectedPosition = pos;
            notifyDataSetChanged();
        }

        List<String> names;
        Context context;
        LayoutInflater inflter;
        int selectedPosition = -1;

        public CustomAdapter(Context context, List<String> names, int selectedPosition) {
            this.context = context;
            this.names = names;
            this.selectedPosition = selectedPosition;
            inflter = (LayoutInflater.from(context));

        }

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            return names.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = inflter.inflate(R.layout.list_dialog_item, null);
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.list_dialog_item);
            simpleCheckedTextView.setText(names.get(position));

            Log.e("selectedPosition", selectedPosition + "---");
            if (selectedPosition == position) {
                simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check);
                simpleCheckedTextView.setChecked(true);
            } else {
                simpleCheckedTextView.setCheckMarkDrawable(null);
                simpleCheckedTextView.setChecked(false);
            }


            return view;
        }
    }


    private void setUploadImage(int index, String url) {

        switch (index) {
            case 0:
                Picasso.with(this).load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imageOne);

                imageOneBtn.setImageResource(R.drawable.pic_delete);

                break;
            case 1:
                Picasso.with(this).load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imageTwo);

                imageTwoBtn.setImageResource(R.drawable.pic_delete);
                DeleteModel deleteModel = new DeleteModel();
                deleteModel.index = index;
                imageTwoBtn.setTag(deleteModel);
                break;
            case 2:
                Picasso.with(this).load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imageThree);

                imageThreeBtn.setImageResource(R.drawable.pic_delete);

                DeleteModel deleteModel2 = new DeleteModel();
                deleteModel2.index = index;
                imageThreeBtn.setTag(deleteModel2);
                break;
            case 3:
                Picasso.with(this).load(url).into(imageFour);

                imageFourBtn.setImageResource(R.drawable.pic_delete);
                DeleteModel deleteModel3 = new DeleteModel();
                deleteModel3.index = index;
                imageFourBtn.setTag(deleteModel3);
                break;
            case 4:
                Picasso.with(this).load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imageFive);

                imageFiveBtn.setImageResource(R.drawable.pic_delete);
                DeleteModel deleteModel4 = new DeleteModel();
                deleteModel4.index = index;
                imageFiveBtn.setTag(deleteModel4);
                break;
            case 5:
                Picasso.with(this).load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imageSix);
                imageSixBtn.setImageResource(R.drawable.pic_delete);
                DeleteModel deleteModel5 = new DeleteModel();
                deleteModel5.index = index;
                imageSixBtn.setTag(deleteModel5);
                break;

        }
    }

}
