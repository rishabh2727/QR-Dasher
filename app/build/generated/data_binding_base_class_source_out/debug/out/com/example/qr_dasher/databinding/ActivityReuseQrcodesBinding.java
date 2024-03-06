// Generated by view binder compiler. Do not edit!
package com.example.qr_dasher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.qr_dasher.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityReuseQrcodesBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button cancelButton;

  @NonNull
  public final LinearLayout container;

  @NonNull
  public final TextView reuseQRcodetext;

  private ActivityReuseQrcodesBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button cancelButton, @NonNull LinearLayout container,
      @NonNull TextView reuseQRcodetext) {
    this.rootView = rootView;
    this.cancelButton = cancelButton;
    this.container = container;
    this.reuseQRcodetext = reuseQRcodetext;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityReuseQrcodesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityReuseQrcodesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_reuse_qrcodes, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityReuseQrcodesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cancel_button;
      Button cancelButton = ViewBindings.findChildViewById(rootView, id);
      if (cancelButton == null) {
        break missingId;
      }

      id = R.id.container;
      LinearLayout container = ViewBindings.findChildViewById(rootView, id);
      if (container == null) {
        break missingId;
      }

      id = R.id.reuseQRcodetext;
      TextView reuseQRcodetext = ViewBindings.findChildViewById(rootView, id);
      if (reuseQRcodetext == null) {
        break missingId;
      }

      return new ActivityReuseQrcodesBinding((ConstraintLayout) rootView, cancelButton, container,
          reuseQRcodetext);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
