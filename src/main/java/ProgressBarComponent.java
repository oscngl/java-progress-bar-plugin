import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.UIManager;

public class ProgressBarComponent implements LafManagerListener, DynamicPluginListener, ApplicationActivationListener {

    private static final String UI_CLASS_NAME = CustomProgressBarUI.class.getName();

    public ProgressBarComponent() {
        updateProgressBarUI();
    }

    @Override
    public void lookAndFeelChanged(@NotNull LafManager source) {
        updateProgressBarUI();
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor ideaPluginDescriptor) {
        updateProgressBarUI();
    }

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        updateProgressBarUI();
    }

    private void updateProgressBarUI() {
        UIManager.put("ProgressBarUI", UI_CLASS_NAME);
        UIManager.getDefaults().put(UI_CLASS_NAME, CustomProgressBarUI.class);
    }

}