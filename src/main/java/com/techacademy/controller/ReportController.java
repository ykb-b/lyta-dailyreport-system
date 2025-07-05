package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    //日報一覧ページ
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        String role = userDetail.getAuthorities().toString();
        if(role.contains("GENERAL")) {
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));
        } else if (role.contains("ADMIN")) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        }
        return "reports/list";
    }

    //日報詳細ページ
    @GetMapping("/{id}/")
    public String detail(@PathVariable("id")Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    //日報登録ページ
    @GetMapping("/add")
    public String create(@ModelAttribute Report report,@AuthenticationPrincipal UserDetail userDetail) {
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        return "reports/add";
    }

    //日報登録処理
    @PostMapping("/add")
    public String add(@Validated Report report, BindingResult res, Model model,@AuthenticationPrincipal UserDetail userDetail) {
        if (res.hasErrors()) {
            return addErr(report);
        }
        Employee emp = userDetail.getEmployee();

        try {
            ErrorKinds result = reportService.save(report, emp);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return addErr(report);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return addErr(report);
        }
        return "redirect:/reports";
    }

    //失敗時登録ページ
    public String addErr(@ModelAttribute Report report) {

        return "reports/add";
    }

    //日報更新ページ
    @GetMapping("/{id}/update")
    public String edit(@PathVariable("id")Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/update";
    }
    //日報更新処理
    @PostMapping("/{id}/update")
    public String update(@Validated Report report, BindingResult res, Model model) {
        if (res.hasErrors()) {
            return updateErr(report);
        }
        ErrorKinds result = reportService.update(report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return updateErr(report);
        }


        return "redirect:/reports";
    }
    //失敗時更新ページ
    public String updateErr(@ModelAttribute Report report) {
        return "reports/update";
    }
    //日報削除
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}
