package com.djrapitops.plan.common.system.tasks.server;

import com.djrapitops.plan.common.PlanHelper;
import com.djrapitops.plan.common.PlanPlugin;
import com.djrapitops.plan.common.system.info.InfoSystem;
import com.djrapitops.plan.common.system.info.connection.WebExceptionLogger;
import com.djrapitops.plan.common.system.info.request.GenerateAnalysisPageRequest;
import com.djrapitops.plan.common.system.info.server.ServerInfo;
import com.djrapitops.plan.common.system.settings.locale.Locale;
import com.djrapitops.plan.common.system.settings.locale.Msg;
import com.djrapitops.plugin.api.utility.log.Log;
import com.djrapitops.plugin.task.AbsRunnable;

public class BootAnalysisTask extends AbsRunnable {

    public BootAnalysisTask() {
        super(BootAnalysisTask.class.getSimpleName());
    }

    @Override
    public void run() {
        try {
            String bootAnalysisRunMsg = Locale.get(Msg.ENABLE_BOOT_ANALYSIS_RUN_INFO).toString();
            Log.info(bootAnalysisRunMsg);
            WebExceptionLogger.logIfOccurs(this.getClass(), () ->
                    InfoSystem.getInstance().sendRequest(new GenerateAnalysisPageRequest(ServerInfo.getServerUUID()))
            );
        } catch (IllegalStateException e) {
            if (!PlanHelper.getInstance().isReloading()) {
                Log.toLog(this.getClass(), e);
            }
        } finally {
            cancel();
        }
    }
}
