package codehows.com.daehoint.dto;

import codehows.com.daehoint.entity.AccessPageHistory;
import codehows.com.daehoint.entity.DownloadHistory;
import codehows.com.daehoint.entity.LoginHistory;
import codehows.com.daehoint.entity.PrintHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogResponse {
    @Data
    @AllArgsConstructor
    public static class LoginLogs {
        private String time;
        private String accessID;
        private String accessIP;

        public static LoginLogs toLoginLogs(LoginHistory loginHistory) {
            String time = String.valueOf(loginHistory.getCreateDateTime());
            return new LoginLogs(time, loginHistory.getAccessId(), loginHistory.getAccessIp());
        }
    }

    @Data
    @AllArgsConstructor
    public static class PageAccessLogs {
        private String time;
        private String accessID;
        private String accessPage;
        private String accessIP;

        public static PageAccessLogs toPageAccessLogs(AccessPageHistory accessPageHistory) {
            String time = String.valueOf(accessPageHistory.getCreateDateTime());
            return new PageAccessLogs(time, accessPageHistory.getAccessId(), accessPageHistory.getAccessPage(), accessPageHistory.getAccessIp());
        }

    }

    @Data
    @AllArgsConstructor
    public static class PrintLogs {
        private String time;
        private String accessID;
        private String printPage;
        private String accessIP;

        public static PrintLogs toPrintLogs(PrintHistory printHistory) {
            String time = String.valueOf(printHistory.getCreateDateTime());
            return new PrintLogs(time, printHistory.getAccessId(), printHistory.getPrintPage(), printHistory.getAccessIp());
        }
    }
    @Data
    @AllArgsConstructor
    public static class DownloadLogs {
        private String time;
        private String accessID;
        private String accessIP;
        private String fileName;

        public static DownloadLogs toDownloadLogs(DownloadHistory downloadHistory) {
            String time = String.valueOf(downloadHistory.getCreateDateTime());
            return new DownloadLogs(time, downloadHistory.getAccessId(), downloadHistory.getAccessIp(), downloadHistory.getFileName());
        }
    }


    private List<LoginLogs> loginLogs;
    private List<PageAccessLogs> pageAccessLogs;
    private List<PrintLogs> printLogs;
    private List<DownloadLogs> downloadLogs;  // 다운로드 로그 추가

}
