package com.devicewifitracker.android.navigation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

/**
 * 处理Bottom Navigation View 下 fragment 切换 回调 onCreateView的问题   但是不起作用
 */
@Navigator.Name("customNavigator")
public class CustomNavigator extends FragmentNavigator {

    private Context context;
    private FragmentManager fragmentManager;
    private int containerId;

    public CustomNavigator(@NonNull Context context, @NonNull FragmentManager fragmentManager, int containerId) {
        super(context, fragmentManager, containerId);

        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // 获取当前显示的Fragment
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        if (fragment != null) {
            ft.hide(fragment);
        }

        final String tag = String.valueOf(destination.getId());
        fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            ft.show(fragment);
        } else {
            fragment = instantiateFragment(context, fragmentManager, destination.getClassName(), args);
            ft.add(containerId, fragment, tag);
        }
        ft.setPrimaryNavigationFragment(fragment);
        ft.setReorderingAllowed(true);
        ft.commit();
        return destination;
    }
}
