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
@Navigator.Name("keep_state_fragment")
public class KeepStateNavigation extends FragmentNavigator {
    private Context context;
    private FragmentManager manager;
    private int containerId;



    public KeepStateNavigation(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.context = context;
        this.manager = manager;
        this.containerId = containerId;
    }



    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        String tag = String.valueOf(destination.getId());
        FragmentTransaction transaction = manager.beginTransaction();
        boolean initialNavigate = false;
        Fragment currentFragment = manager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        } else {
            initialNavigate = true;
        }
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            String className = destination.getClassName();
            fragment = manager.getFragmentFactory().instantiate(context.getClassLoader(), className);
            transaction.add(containerId, fragment, tag);
        } else {
            transaction.show(fragment);
        }

        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commitNow();
        return initialNavigate ? destination : null;
    }

//    @Override
//    public boolean popBackStack() {
//        if (mBack)
//        return true
//    }
}
