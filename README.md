# 实现JAVA对象与CSV格式数据互转

## 一、背景

正对采集业务，在互联网下，需要完成高效的数据传输，尽可能的压缩采集的数据，达到传输的高效，传输相同数据，占用更小的带宽。

在此，实现两种策略，使用采集约定的达到高效的数据传输的能力。

采集端：

1、将java对象转成CSV格式数据

2、对CSV格式数据在进行压缩

解析端：

3、将压缩数据解压成CSV数据

4、将CSV数据转成java对象

## 二、开发环境

JDK 1.8.131

引入核心jar包：

```java
jackson-dataformat-csv
jackson-core
jackson-annotations
jackson-databind
```

使用jackson原因：

1、Spring MVC 的默认 json 解析器便是 Jackson。且jackson的性能和稳定性都值得信赖。

2、jackson提供了java对象与各自格式的数据的互转能力。包括了csv,xml,yaml,properties,protobuf等。

## 三、案例目标

采集端：

1、将java对象转成CSV格式数据

2、对CSV格式数据在进行压缩

解析端：

3、将压缩数据解压成CSV数据

4、将CSV数据转成java对象

## 四、技术实现

1、提供csv的工具类

```java
public class CsvUtil {

    private static final Logger logger = LogManager.getLogger(CsvUtil.class);

    private CsvUtil() {

    }

    /**
     * 输出全部属性
     * 如果csv中存在，对象中没有，则自动忽略该属性
     * 失败返回空list
     *
     * @param schema     schema
     * @param csvContent cvs字符串
     * @param clazz      类
     * @return list<T> 对象列表
     */
    public static <T> List<T> parseObject(CsvSchema schema, String csvContent, Class<T> clazz) {
        Assert.notNull(schema, "schema must not be null");
        Assert.hasText(csvContent, "csvContent must not be null or empty");
        Assert.notNull(clazz, "clazz must not be null");
        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<T> mappingIterator = csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readerFor(clazz).with(schema).readValues(csvContent)) {
            return mappingIterator.readAll();
        } catch (IOException e) {
            logger.error("CsvToObject failed!", e);
        }
        return Collections.emptyList();
    }

    /**
     * 输出全部属性
     * 如果csv中存在，对象中没有，则自动忽略该属性
     * 失败返回空list
     *
     * @param schema schema
     * @param bytes  字节数组
     * @param clazz  类
     * @return list<T> 对象列表
     */
    public static <T> List<T> parseObject(CsvSchema schema, byte[] bytes, Class<T> clazz) {
        Assert.notNull(schema, "schema must not be null");
        Assert.isTrue(bytes.length > 0, "bytes length > 0");
        Assert.notNull(clazz, "clazz must not be null");
        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<T> mappingIterator = csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readerFor(clazz).with(schema).readValues(bytes)) {
            return mappingIterator.readAll();
        } catch (IOException e) {
            logger.error("CsvToObject failed!", e);
        }
        return Collections.emptyList();
    }


    /**
     * 对象转成csv格式，返回字符串
     *
     * @param csvSchema csvSchema
     * @param object    对象
     * @return string
     */
    public static String toCsv(CsvSchema csvSchema, Object object) {
        Assert.notNull(csvSchema, "schema must not be null");
        Assert.notNull(object, "object must not be null");
        try {
            CsvMapper csvMapper = new CsvMapper();
            return csvMapper.writer(csvSchema).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("ObjToCsv failed！", e);
        }
        return "";
    }

}
```

2、提供字符串压缩和解压的工具类

GZIPOutputStream和DeflaterOutputStream有什么区别？

Deflate可以被认为是压缩算法的参考实现,而ZIP和GZIP则是“扩展”.后者支持“档案”的概念,这在ZipOutputStream的Java API中更加明显.

如果你只是想压缩数据流,我建议使用DeflaterOutputStream,但是如果你正在创建一个存档,你应该看看ZipOutputStream.

```java
public class ZipUtil {

    private static final Logger LOGGER = LogManager.getLogger(CsvUtil.class);

    private ZipUtil() {
    }

    /**
     * 压缩字符串
     *
     * @param content 压缩前字符串
     * @return byte[] 压缩后数组
     * @throws IOException 压缩异常
     */
    public static byte[] deflater(String content) throws IOException {
        Assert.hasText(content, "content must not be null or empty");
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(byteArrayOutputStream, def)) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            dos.write(bytes);
            dos.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 解压字节数组
     *
     * @param bytes 字节数组
     * @return string 解压后的字符串
     */
    public static String inflater(byte[] bytes) throws IOException {
        Assert.isTrue(bytes.length > 0, "bytes length > 0");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater inf = new Inflater(true);
        try (InflaterInputStream iis = new InflaterInputStream(bis, inf)) {
            int readCount = 0;
            byte[] buf = new byte[1024];
            while ((readCount = iis.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, readCount);
            }
            bos.flush();
        }

        return bos.toString(StandardCharsets.UTF_8.name());
    }

}
```

3、案例执行（demo引了springboot实际一个main执行即可验证）

定义一个CsvSchema描述，去掉withUseHeader。

```java
public class DataCsvService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCsvService.class);

    public static void main(String[] args) throws IOException {
        List<PersonDTO> pojoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pojoList.add(new PersonDTO("firstName:姓" + i, i + 10, SexEnum.getSexEnumByType(i % 3), new Date()));
        }

        CsvSchema schema = CsvSchema.builder()
                .addColumn("name")
                .addColumn("age", CsvSchema.ColumnType.NUMBER)
                .addColumn("sexEnum", CsvSchema.ColumnType.NUMBER)
                .addColumn("birthday", CsvSchema.ColumnType.STRING)
                .build().withUseHeader(false);

        String csv = CsvUtil.toCsv(schema, pojoList);
        LOGGER.info("java对象转成csv数据:\n{}", csv);

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        byte[] deflater = ZipUtil.deflater(csv);
        LOGGER.info("csv数据源数组长度为:{}，csv数据压缩成byte数组长度为:{}", bytes.length, deflater.length);
        String inflater = ZipUtil.inflater(deflater);
        LOGGER.info("压缩数据还原成正常csv数据为:\n{}", inflater);
        List<PersonDTO> personDTOS = CsvUtil.parseObject(schema, inflater, PersonDTO.class);
        LOGGER.info("正常csv数据转成java对象为:\n{}", personDTOS);

    }
}
```

效果：

![image-20210508150035742](https://raw.githubusercontent.com/lqnasa/springboot-data-csv-compression/master/docs/images/image-20210508150035742.png)



## 五、总结

1、java对象转成csv格式，需要采集服务与清洗服务，约定规则和格式。（spring中有约定优于配置原则，所以约定下规则更高效）

2、压缩效率肯定跟数据中相同的字符相关。相同内容越多，压缩比越大。且这里才能用最佳压缩模式。以时间换压缩比率。

项目源码：

https://github.com/FasterXML/jackson-dataformats-text
