package com.automate.AutoJobApply.service;

import com.automate.AutoJobApply.model.Job;
import com.automate.AutoJobApply.model.JobMatchResult;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;

@Service
public class ExcelService {

    public void generateExcel(List<Job> jobs, List<JobMatchResult> results) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Jobs");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Title");
            header.createCell(1).setCellValue("Company");
            header.createCell(2).setCellValue("Score");
            header.createCell(3).setCellValue("Decision");
            header.createCell(4).setCellValue("URL");
//            header.createCell(5).setCellValue("Email");

            for (int i = 0; i < jobs.size(); i++) {
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(jobs.get(i).getTitle());
                row.createCell(1).setCellValue(jobs.get(i).getCompany());
                row.createCell(2).setCellValue(results.get(i).getScore());
                row.createCell(3).setCellValue(results.get(i).getDecision());
                row.createCell(4).setCellValue(jobs.get(i).getUrl());
//                row.createCell(5).setCellValue(emailContent);
            }

            FileOutputStream fileOut = new FileOutputStream("jobs.xlsx");
            workbook.write(fileOut);
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}