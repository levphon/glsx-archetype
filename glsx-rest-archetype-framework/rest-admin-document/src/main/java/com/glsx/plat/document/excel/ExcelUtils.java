package com.glsx.plat.document.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.TableStyle;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtils {

    /**
     * 读取 Excel(多个 sheet)
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);

        if (reader == null) {
            return null;
        }

        for (Sheet sheet : reader.getSheets()) {
            if (rowModel != null) {
                sheet.setClazz(rowModel.getClass());
            }
            reader.read(sheet);
        }

        return excelListener.getDatas();
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @param sheetNo  sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo) {
        return readExcel(excel, rowModel, sheetNo, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo, int headLineNum) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);

        if (reader == null) {
            return null;
        }

        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));

        return excelListener.getDatas();
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     *
     * @param response  HttpServletResponse
     * @param list      数据 list，每个元素为一个 BaseRowModel
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射实体类，Excel 模型
     */
    public static void writeExcel(HttpServletResponse response, List<? extends BaseRowModel> list, String fileName,
                                  String sheetName, BaseRowModel object) throws Exception {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);

        TableStyle tableStyle = new TableStyle();
        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        Font font = new Font();
        font.setFontHeightInPoints((short) 9);
        tableStyle.setTableHeadFont(font);
        tableStyle.setTableContentFont(font);
        sheet.setTableStyle(tableStyle);

        writer.write(list, sheet);
        writer.finish();
    }

    /**
     * 生成excel
     *
     * @param fullPathName
     * @param list
     * @param model
     */
    public static void writeExcel(String fullPathName, List<? extends BaseRowModel> list, BaseRowModel model) {
        // 文件输出位置
        OutputStream out = null;
        try {
            out = new FileOutputStream(fullPathName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ExcelWriter writer = EasyExcelFactory.getWriter(out);

        // 写仅有一个 Sheet 的 Excel 文件, 此场景较为通用
        Sheet sheet1 = new Sheet(1, 0, model.getClass());

        // 第一个 sheet 名称
        sheet1.setSheetName("Sheet1");

        // 写数据到 Writer 上下文中
        // 入参1: 创建要写入的模型数据
        // 入参2: 要写入的目标 sheet
        writer.write(list, sheet1);

        // 将上下文中的最终 outputStream 写入到指定文件中
        writer.finish();

        // 关闭流
        IOUtils.closeQuietly(out);
    }

    /**
     * 导出
     *
     * @param response
     * @param data
     * @param fileName
     * @param sheetName
     * @param clazz
     * @throws Exception
     */
    public static void writeExcel(HttpServletResponse response, List<? extends Object> data, String fileName, String sheetName, Class clazz) throws Exception {
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteFont writeFont = new WriteFont();
        writeFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(writeFont);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), clazz).excelType(ExcelTypeEnum.XLSX).sheet(sheetName).registerWriteHandler(horizontalCellStyleStrategy).doWrite(data);
        response.getOutputStream().flush();
        IOUtils.closeQuietly(response.getOutputStream());
    }

    private static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ExcelTypeEnum.XLSX.getValue());
        response.setContentType("application/msexcel;charset=UTF-8");//设置类型
        response.setHeader("Pragma", "public");//设置头
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");
        response.setHeader("Cache-Control", "no-cache");//设置头
        response.setDateHeader("Expires", 0);//设置日期头
        return response.getOutputStream();
    }

    /**
     * 导出 Excel ：多个 sheet，带表头
     *
     * @param response  HttpServletResponse
     * @param list      数据 list，每个元素为一个 BaseRowModel
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射实体类，Excel 模型
     */
    public static ExcelWriterFactory writeExcelWithSheets(HttpServletResponse response,
                                                          List<? extends BaseRowModel> list, String fileName,
                                                          String sheetName, BaseRowModel object) throws Exception {
        ExcelWriterFactory writer = new ExcelWriterFactory(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);
        sheet.setTableStyle(getTableStyle());
        writer.write(list, sheet);

        return writer;
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel         需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(MultipartFile excel, ExcelListener<T> excelListener) {
        String filename = excel.getOriginalFilename();

        if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
            throw new RuntimeException(new IOException("文件格式错误！"));
        }
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());

            return new ExcelReader(inputStream, null, excelListener, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Excel表格数据，封装成实体
     *
     * @param inputStream
     * @param clazz
     * @param sheetNo
     * @param headLineMun
     * @return
     */
    public static Object readExcel(InputStream inputStream, Class<? extends BaseRowModel> clazz, Integer sheetNo, Integer headLineMun) {
        if (null == inputStream) {
            throw new NullPointerException("the InputStream is null!");
        }

        ExcelListener listener = new ExcelListener();
        ExcelReader reader = new ExcelReader(inputStream, valueOf(inputStream), null, listener);
        reader.read(new Sheet(sheetNo, headLineMun, clazz));

        return listener.getDatas();
    }

    /**
     * @param inputStream 根据输入流，判断为xls还是xlsx，该方法原本存在于easyexcel 1.1.0 的ExcelTypeEnum中。easyexcel 2.xx版本无需判断版本
     */
    public static ExcelTypeEnum valueOf(InputStream inputStream) {
        try {
            FileMagic fileMagic = FileMagic.valueOf(inputStream);

            if (FileMagic.OLE2.equals(fileMagic)) {
                return ExcelTypeEnum.XLS;
            }

            if (FileMagic.OOXML.equals(fileMagic)) {
                return ExcelTypeEnum.XLSX;
            }

            throw new IOException("ExcelTypeEnum can not null");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置全局样式
     *
     * @return
     */
    private static TableStyle getTableStyle() {
        TableStyle tableStyle = new TableStyle();

        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        Font font = new Font();
        font.setBold(true);
        font.setFontHeightInPoints((short) 9);
        tableStyle.setTableHeadFont(font);
        Font fontContent = new Font();
        fontContent.setFontHeightInPoints((short) 9);
        tableStyle.setTableContentFont(fontContent);

        return tableStyle;
    }

    /**
     * 读取xls文件，将数据信息保存到map中
     *
     * @param inputStream
     * @param sheetNum     第一个sheet
     * @param headerRowNum 第一行
     * @param rowModel
     * @return
     * @throws Exception
     */
    public static List<Object> readExcel(InputStream inputStream, int sheetNum, int headerRowNum, BaseRowModel rowModel) throws Exception {

        /**
         * 自定义用于暂时存储data。
         * 可以通过实例获取该值
         */
        List<Object> datas = new ArrayList<>();

        if (sheetNum < 0) return datas;
        if (headerRowNum < 0) return datas;

        Workbook wb = new XSSFWorkbook(inputStream);

        org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(sheetNum - 1);

        Row headerRow = sheet.getRow(headerRowNum - 1);//提取表头

        for (int j = 0; j <= sheet.getLastRowNum(); j++) {//遍历行
            if (j == (headerRowNum - 1)) continue;//第j行为表头时跳过

            Class clazz = rowModel.getClass();
            Object obj = clazz.newInstance();

            Row row = sheet.getRow(j);
            if (row != null) {
                for (int k = 0; k < row.getLastCellNum(); k++) {//遍历单元格
                    String key = headerRow != null ? headerRow.getCell(k).getStringCellValue() : "";
                    if (StringUtils.isEmpty(key)) key = String.valueOf(k);

                    String value = readStringExcelCell(sheet, j, k);

                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                        if (!key.equals(property.value()[0])) continue;

                        String fieldName = field.getName();
                        String valueFormat = property.format();
                        Object val = parseType(value, valueFormat, field.getType());

                        String methodName = "set" + upperCase(fieldName);
                        Method mtd = clazz.getDeclaredMethod(methodName, field.getType());
                        mtd.invoke(obj, val);
                        break;
                    }
                }
                datas.add(obj);
            }
        }
        return datas;
    }

    // 首字母变大写
    private static String upperCase(String str) {
        char[] ch = str.toCharArray();
//      也可以直接用下面的记性转大写
//      ch[0] = Character.toUpperCase(ch[0]);
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 类型转换
     *
     * @param value  原始数据格式
     * @param format 期待转换的格式
     * @param type   期待转换的类型
     * @return 转换后的数据对象
     */
    private static Object parseType(String value, String format, Class type) {

        if (type == String.class) {
            return value;
        } else if (type == int.class) {
            return value == null ? 0 : Integer.parseInt(value);
        } else if (type == float.class) {
            if (value == null) return 0f;
            if (value.contains("%")) value = value.replace("%", "").trim();
            return Float.parseFloat(value) / 100;
        } else if (type == long.class) {
            return value == null ? 0L : Long.parseLong(value);
        } else if (type == double.class) {
            return value == null ? 0D : Double.parseDouble(value);
        } else if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == byte.class) {
            return value == null || value.length() == 0 ? 0 : value.getBytes()[0];
        } else if (type == char.class) {
            if (value == null || value.length() == 0) {
                return 0;
            }

            char[] chars = new char[1];
            value.getChars(0, 1, chars, 0);
            return chars[0];
        }

        // 非基本类型,
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (type == Integer.class) {
            return Integer.valueOf(value);
        } else if (type == Long.class) {
            return Long.valueOf(value);
        } else if (type == Float.class) {
            if (value.contains("%")) value = value.replace("%", "").trim();
            return Float.parseFloat(value) / 100;
        } else if (type == Double.class) {
            return Double.valueOf(value);
        } else if (type == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == Byte.class) {
            return value.getBytes()[0];
        } else if (type == Character.class) {
            char[] chars = new char[1];
            value.getChars(0, 1, chars, 0);
            return chars[0];
        } else if (type == BigDecimal.class) {
            return new BigDecimal(value);
        } else if (type == Date.class) {
            try {
                return DateUtils.parseDate(value, format);
            } catch (ParseException e) {
                return null;
            }
        }
        throw new IllegalStateException("argument not basic type! now type:" + type.getName());
    }

    /**
     * 读取单元格
     *
     * @param sheet
     * @param rowNum
     * @param cellNum
     * @return
     */
    public static String readStringExcelCell(org.apache.poi.ss.usermodel.Sheet sheet, int rowNum, int cellNum) {
        String value = "";
        if (rowNum < 0) return value;
        try {
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(cellNum);
            if (null != cell) {
//                _NONE(-1),
//                NUMERIC(0),
//                STRING(1),
//                FORMULA(2),
//                BLANK(3),
//                BOOLEAN(4),
//                ERROR(5);

                switch (row.getCell(cellNum).getCellTypeEnum()) {
                    case FORMULA:
                        try {
                            /*
                             * 此处判断使用公式生成的字符串有问题，因为HSSFDateUtil.isCellDateFormatted(cell)判断过程中cell
                             * .getNumericCellValue();方法会抛出java.lang.NumberFormatException异常
                             */
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                value = DateUtils.format(date);
                                break;
                            } else {
                                value = String.valueOf(cell.getNumericCellValue());
                            }
                        } catch (IllegalStateException e) {
                            value = cell.getCellFormula();
//                            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
//                            value = getCellValue(evaluator.evaluateInCell(cell));
                        }
                        break;
                    case NUMERIC:
                        if (HSSFDateUtil.isCellDateFormatted(row.getCell(cellNum))) {
                            Date date = cell.getDateCellValue();
                            value = DateUtils.format(date);
                        } else {
                            value = String.valueOf(cell.getNumericCellValue());
                        }
                        break;
                    case STRING:
                        value = cell.getRichStringCellValue().getString();
                        break;
                    case BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case BLANK:
                        value = "";
                        break;
                    default:
                        break;
                }
                //如果读取的是科学计数法的格式，则转换为普通格式
                if (value.contains(".") && value.contains("E")) {
                    DecimalFormat df = new DecimalFormat();
                    value = df.parse(value).toString();
                }
                //如果读取的是数字格式，并且以".0"结尾格式，则转换为普通格式
                if (null != value && value.endsWith(".0")) {
                    int size = value.length();
                    value = value.substring(0, size - 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

//    public static void main(String[] args) throws Exception {
//        FileInputStream fis = new FileInputStream(new File("C:\\Users\\payu\\Desktop\\财务2020-04-14.xlsx"));
//        List<Object> excelData = readExcel(fis, 1, 1, new ReconcileImport());
//        System.out.println(excelData);
//    }

}
