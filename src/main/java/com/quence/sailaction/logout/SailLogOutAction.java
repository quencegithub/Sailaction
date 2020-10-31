package com.quence.sailaction.logout;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public class SailLogOutAction implements Action {

    private static final String BUNDLE_NAME = "com.quence.sailaction.logout.bundle";
    private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
    private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");
    private static final ImageIcon DISPLAY_ICON = new ImageIcon(SailLogOutAction.class.getResource(ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("iconFile")));

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "Sail logout";
    }

    @Override
    public List<ActionParameter> getDefaultActionParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends ActionEngine> getEngineClass() {
        // TODO Auto-generated method stub
        return SailLogOutActionEngine.class;
    }

    @Override
    public Icon getIcon() {
        // TODO Auto-generated method stub
        return DISPLAY_ICON;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return " Sail Logout Action ";
    }

    @Override
    public String getDisplayName() {
        // TODO Auto-generated method stub
        return DISPLAY_NAME;
    }

    @Override
    public String getDisplayPath() {
        // TODO Auto-generated method stub
        return DISPLAY_PATH;
    }

    @Override
    public Optional<String> getMinimumNeoLoadVersion() {
        // TODO Auto-generated method stub
        return Optional.absent();
    }

    @Override
    public Optional<String> getMaximumNeoLoadVersion() {
        // TODO Auto-generated method stub
        return Optional.absent();
    }

}
