package com.zemrow.test.work_schedule.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import com.zemrow.test.work_schedule.DataSourceWrapper;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDto;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDtoHoliday;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDtoUser;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDtoUserVacation;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDtoUserWork;
import com.zemrow.test.work_schedule.dao.HolidayDao;
import com.zemrow.test.work_schedule.dao.UserDao;
import com.zemrow.test.work_schedule.dao.VacationDao;
import com.zemrow.test.work_schedule.dao.WorkScheduleDao;
import com.zemrow.test.work_schedule.entity.Holiday;
import com.zemrow.test.work_schedule.entity.User;
import com.zemrow.test.work_schedule.entity.Vacation;
import com.zemrow.test.work_schedule.entity.VacationType;
import com.zemrow.test.work_schedule.entity.WorkSchedule;
import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Контроллер графика работы
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleController {

    private final DataSourceWrapper dataSource;

    private final HolidayDao holidayDao;
    private final UserDao userDao;
    private final WorkScheduleDao workScheduleDao;
    private final VacationDao vacationDao;

    public WorkScheduleController(DataSourceWrapper dataSource,
        HolidayDao holidayDao,
        UserDao userDao,
        WorkScheduleDao workScheduleDao,
        VacationDao vacationDao) {
        this.dataSource = dataSource;
        this.holidayDao = holidayDao;
        this.userDao = userDao;
        this.workScheduleDao = workScheduleDao;
        this.vacationDao = vacationDao;
    }

    public WorkScheduleDto getWorkScheduleDto(int year, int month) {
        final WorkScheduleDto result = new WorkScheduleDto();
        result.setYear(year);
        result.setMonth(month);
        long startTime = new GregorianCalendar(year, month - 1, 1).getTimeInMillis();
        long endTime = new GregorianCalendar(year, month, 1).getTimeInMillis() - 1;

        try (final Connection connection = dataSource.getConnection()) {
            final List<Holiday> holidays = holidayDao.selectByPeriod(connection, startTime, endTime);
            final List<WorkScheduleDtoHoliday> resultHolidays = new ArrayList<>(holidays.size());
            for (Holiday holiday : holidays) {
                final WorkScheduleDtoHoliday item = new WorkScheduleDtoHoliday();
                item.setHolidayId(holiday.getId());
                item.setTitle(holiday.getTitle());
                item.setStartTime(holiday.getStartTime());
                item.setEndTime(holiday.getEndTime());
                resultHolidays.add(item);
            }
            result.setHolidays(resultHolidays);
            //TODO
            final List<User> users = userDao.select(connection, 0L, 20);
            final List<WorkScheduleDtoUser> resultUsers = new ArrayList<>(users.size());
            final List<WorkSchedule> works = workScheduleDao.selectByPeriod(connection, startTime, endTime);
            final List<Vacation> vacations = vacationDao.selectByPeriod(connection, startTime, endTime);
            for (User user : users) {
                final WorkScheduleDtoUser item = new WorkScheduleDtoUser();
                item.setUserId(user.getId());
                item.setFullName(user.getFullName());
                final List<WorkScheduleDtoUserWork> resultWorks = new ArrayList<>();
                for (WorkSchedule work : works) {
                    if (work.getUserId().equals(user.getId())) {
                        final WorkScheduleDtoUserWork resultWork = new WorkScheduleDtoUserWork();
                        resultWork.setWorkScheduleId(work.getId());
                        resultWork.setStartTime(work.getStartTime());
                        resultWork.setEndTime(work.getEndTime());
                        resultWorks.add(resultWork);
                    }
                }
                item.setWorks(resultWorks);

                final List<WorkScheduleDtoUserVacation> resultVacations = new ArrayList<>();
                for (Vacation vacation : vacations) {
                    if (vacation.getUserId().equals(user.getId())) {
                        final WorkScheduleDtoUserVacation resultVacation = new WorkScheduleDtoUserVacation();
                        resultVacation.setVacationId(vacation.getId());
                        resultVacation.setStartTime(vacation.getStartTime());
                        resultVacation.setEndTime(vacation.getEndTime());
                        resultVacation.setType(vacation.getVacationType());
                        resultVacations.add(resultVacation);
                    }
                }
                item.setVacationList(resultVacations);
                resultUsers.add(item);
            }
            result.setUsers(resultUsers);
        }
        catch (Exception e) {
            //TODO
            throw new RuntimeException(e);
        }
        return result;
    }

    public WorkSchedule insert(long userId, long startTime, long endTime) {
        try (final Connection connection = dataSource.getConnection()) {
            final List<WorkSchedule> findWorks = workScheduleDao.selectByUserAndPeriod(connection, userId, startTime, endTime);
            if (findWorks.isEmpty()) {
                final WorkSchedule result = new WorkSchedule(userId, startTime, endTime);
                workScheduleDao.insert(connection, result);
                connection.commit();
                return result;
            }
            else {
                // concat
                boolean needUpdate = false;
                for (WorkSchedule work : findWorks) {
                    needUpdate = needUpdate || work.getStartTime() != startTime;
                    if (work.getStartTime() < startTime) {
                        startTime = work.getStartTime();
                    }
                    needUpdate = needUpdate || work.getEndTime() != endTime;
                    if (work.getEndTime() > endTime) {
                        endTime = work.getEndTime();
                    }
                }
                if (needUpdate) {
                    final WorkSchedule work = findWorks.get(0);
                    work.setStartTime(startTime);
                    work.setEndTime(endTime);
                    workScheduleDao.update(connection, work);
                }
                for (int i = 1; i < findWorks.size(); i++) {
                    workScheduleDao.delete(connection, findWorks.get(i).getId());
                }
                connection.commit();
                return findWorks.get(0);
            }
        }
        catch (SQLException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public void delete(long id) {
        try (final Connection connection = dataSource.getConnection()) {
            workScheduleDao.delete(connection, id);
            connection.commit();
        }
        catch (SQLException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public Workbook export(int year, int month) {

        final WorkScheduleDto dto = getWorkScheduleDto(year, month);

        Workbook result = new XSSFWorkbook();

        final CellStyle styleHead = result.createCellStyle();
        styleHead.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHead.setAlignment(HorizontalAlignment.CENTER);

        final CellStyle styleBold = result.createCellStyle();
        final Font fontBold = result.createFont();
        fontBold.setBold(true);
        styleBold.setFont(fontBold);

        final Locale ru = new Locale("ru");
        final String monthName = Month.of(month - 1).getDisplayName(TextStyle.FULL_STANDALONE, ru);
        final Sheet sheet = result.createSheet(monthName);
        int rownum = 0;
        Row row = sheet.createRow(rownum);
        int column = 1;
        row.createCell(column).setCellValue("График за "+monthName);

        row = sheet.createRow(++rownum);
        column = 0;
        Cell cell = row.createCell(column);
        cell.setCellValue("Таб. №");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column++));
        cell = row.createCell(column);
        cell.setCellValue("ФИО");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column++));

        final int lastDay = new GregorianCalendar(year, month, 0).get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= lastDay; i++) {
            cell = row.createCell(column++);
            cell.setCellValue(i);
            cell.setCellStyle(styleHead);
        }
        cell = row.createCell(column);
        cell.setCellValue("Факт");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column++));
        cell = row.createCell(column);
        cell.setCellValue("Норма");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column++));
        cell = row.createCell(column);
        cell.setCellValue("Переработка");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column++));
        cell = row.createCell(column);
        cell.setCellValue("Ночь");
        cell.setCellStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, column, column));

        row = sheet.createRow(++rownum);
        column = 2;
        int firstDayOfWeek = new GregorianCalendar(year, month - 1, 1).get(Calendar.DAY_OF_WEEK)-1;
        if (firstDayOfWeek==0){
            firstDayOfWeek=7;
        }
        int dayOfWeek=firstDayOfWeek;
        int normHours = 0;
        for (int i = 1; i <= lastDay; i++) {
            cell = row.createCell(column++);
            cell.setCellValue(DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.SHORT, ru));
            if (dayOfWeek<6){
                normHours+=8;
                cell.setCellStyle(styleHead);
            }else{
                cell.setCellStyle(styleBold);
            }
            dayOfWeek++;
            if (dayOfWeek > 7) {
                dayOfWeek = 1;
            }
        }

        sheet.createFreezePane( 2, 3);

        for (WorkScheduleDtoUser user : dto.getUsers()) {
            row = sheet.createRow(++rownum);
            column = 0;
            row.createCell(column++).setCellValue(user.getUserId());
            row.createCell(column++).setCellValue(user.getFullName());
            float sum_hours = 0;
            int normHoursForUser=normHours;
            int night_hours = 0;
            dayOfWeek=firstDayOfWeek;
            for (int j = 1; j <= lastDay; j++) {
                long workTime0_10 = new GregorianCalendar(year, month - 1, j, 5, 0).getTimeInMillis();
                long workTime10_19 = new GregorianCalendar(year, month - 1, j, 13, 0).getTimeInMillis();
                long workTime19_22 = new GregorianCalendar(year, month - 1, j, 20, 0).getTimeInMillis();
                long workTime22_24 = new GregorianCalendar(year, month - 1, j, 23, 0).getTimeInMillis();
                Long workTime0_10Id=null;
                Long workTime10_19Id=null;
                Long workTime19_22Id=null;
                Long workTime22_24Id=null;
                float sumInDay=0;
                for (WorkScheduleDtoUserWork work:user.getWorks()){
                    if (work.getStartTime() <= workTime0_10 && work.getEndTime() >= workTime0_10) {
                        workTime0_10Id = work.getWorkScheduleId();
                        sumInDay+=10;
                        sum_hours += 10;
                        night_hours += 6;
                    }
                    if (work.getStartTime() <= workTime10_19 && work.getEndTime() >= workTime10_19) {
                        workTime10_19Id = work.getWorkScheduleId();
                        sumInDay+=8;
                        sum_hours += 8;
                    }
                    if (work.getStartTime() <= workTime19_22 && work.getEndTime() >= workTime19_22) {
                        workTime19_22Id = work.getWorkScheduleId();
                        sumInDay+=4;
                        sum_hours += 4;
                    }
                    if (work.getStartTime() <= workTime22_24 && work.getEndTime() >= workTime22_24) {
                        workTime22_24Id = work.getWorkScheduleId();
                        sumInDay+=2;
                        sum_hours += 2;
                        night_hours += 2;
                    }
                }
                if (sumInDay>12){
                    sumInDay-=0.5;
                    sum_hours-=0.5;
                }

                VacationType vacationType=null;
                for (WorkScheduleDtoUserVacation vacation:user.getVacationList()) {
                    if (vacation.getStartTime() <= workTime10_19&& vacation.getEndTime() >= workTime10_19) {
                        vacationType = vacation.getType();
                    }
                }
                cell = row.createCell(column);
                if (vacationType==VacationType.VACATION){
                    cell.setCellValue("Отп");
                    if (dayOfWeek<6){
                        normHoursForUser-=8;
                    }
                }else
                if (vacationType==VacationType.UNPAID){
                    cell.setCellValue("Нео");
                    if (dayOfWeek<6) {
                        normHoursForUser -= 8;
                    }
                }else
                if (vacationType==VacationType.HOSPITAL){
                    cell.setCellValue("Бол");
                    if (dayOfWeek<6) {
                        normHoursForUser -= 8;
                    }
                }else
                if (sumInDay>0) {
                    cell.setCellValue(sumInDay);
                }
                column++;
                dayOfWeek++;
                if (dayOfWeek > 7) {
                    dayOfWeek = 1;
                }
            }
            row.createCell(column++).setCellValue(sum_hours);
            row.createCell(column++).setCellValue(normHoursForUser);
            row.createCell(column++).setCellValue(sum_hours - normHoursForUser);
            row.createCell(column++).setCellValue(night_hours);
        }

        for (int i=1;i<lastDay+2;i++) {
            sheet.autoSizeColumn(i);
        }

        // border
        final PropertyTemplate pt = new PropertyTemplate();
        pt.drawBorders(new CellRangeAddress(1, dto.getUsers().size()+2, 0, lastDay+5),
            BorderStyle.THIN, BorderExtent.ALL);
        pt.applyBorders(sheet);

        final PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
//        sheet.setAutobreaks(true);
        ps.setFitWidth((short)1);

        return result;
    }
}
