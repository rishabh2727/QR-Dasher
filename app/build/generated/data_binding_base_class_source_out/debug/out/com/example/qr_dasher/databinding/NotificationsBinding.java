// Generated by view binder compiler. Do not edit!
package com.example.qr_dasher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public final class NotificationsBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button clearNotificationsButton;

  @NonNull
  public final Button enableDisableButton;

  @NonNull
  public final TextView notificationText;

  private NotificationsBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button clearNotificationsButton, @NonNull Button enableDisableButton,
      @NonNull TextView notificationText) {
    this.rootView = rootView;
    this.clearNotificationsButton = clearNotificationsButton;
    this.enableDisableButton = enableDisableButton;
    this.notificationText = notificationText;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NotificationsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NotificationsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.notifications, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NotificationsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.clear_notifications_button;
      Button clearNotificationsButton = ViewBindings.findChildViewById(rootView, id);
      if (clearNotificationsButton == null) {
        break missingId;
      }

      id = R.id.enable_disable_button;
      Button enableDisableButton = ViewBindings.findChildViewById(rootView, id);
      if (enableDisableButton == null) {
        break missingId;
      }

      id = R.id.notification_text;
      TextView notificationText = ViewBindings.findChildViewById(rootView, id);
      if (notificationText == null) {
        break missingId;
      }

      return new NotificationsBinding((ConstraintLayout) rootView, clearNotificationsButton,
          enableDisableButton, notificationText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
