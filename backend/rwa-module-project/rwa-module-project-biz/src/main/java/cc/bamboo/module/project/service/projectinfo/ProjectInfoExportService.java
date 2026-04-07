package cc.bamboo.module.project.service.projectinfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ProjectInfoExportService {

    void exportProjectSummaryExcel(List<Long> projectIds, HttpServletResponse response) throws IOException;
}

