package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    //管理者ユーザの日報検索
    public List<Report> findAll(){
        return reportRepository.findAll();
    }
    //一般ユーザの日報検索
    public List<Report> findByEmployee(Employee employee){
        return reportRepository.findByEmployee(employee);
    }
    //日報１件検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        Report report = option.orElse(null);
        return report;
    }
    //日報登録
    @Transactional
    public ErrorKinds save(Report report, Employee emp) {
        ErrorKinds result = checkDate(report, emp);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }
        report.setEmployee(emp);
        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    //レポート更新
    @Transactional
    public ErrorKinds update(Report report) {
        //日報IDからデータベースに現在登録されているレポートを取り出す
        Report dbReport = findById(report.getId());
        //登録されている日報と登録しようとしている日報の指定日付が異なる場合日付チェックを行う
        if(!report.getReportDate().equals(dbReport.getReportDate())) {
            Employee emp = dbReport.getEmployee();
            ErrorKinds result = checkDate(report, emp);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        }
        dbReport.setReportDate(report.getReportDate());
        dbReport.setTitle(report.getTitle());
        dbReport.setContent(report.getContent());
        LocalDateTime now = LocalDateTime.now();
        dbReport.setUpdatedAt(now);
        reportRepository.save(dbReport);
        return ErrorKinds.SUCCESS;
    }

    //日報削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
    }
    //日付チェック
    public ErrorKinds checkDate(Report report, Employee emp) {
        //日付検索しログインユーザと同一のユーザが既に登録していた場合エラー
        LocalDate ckDate = report.getReportDate();
        List<Report> checkDate = reportRepository.findByEmployeeAndReportDate(emp, ckDate);
        if(checkDate.size() != 0) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        return ErrorKinds.CHECK_OK;
    }
}

