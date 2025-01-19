package LinkUpTalk.common.util;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class FileUtil {/*

    @Value("${file.store}")
    private String uploadPath;

    public String test = uploadPath;

    public File upload(MultipartFile uploadFile, int boardId) throws IOException {

        Optional<String> OptOriginalFileName = Optional.ofNullable(uploadFile.getOriginalFilename());
        File file = null;
        if (OptOriginalFileName.isPresent()) {

            String originalFileName = OptOriginalFileName.get();
            UUID uuid = UUID.randomUUID();
            String saveFileName = uuid.toString() + "_" +
                    originalFileName.substring(originalFileName.lastIndexOf("\\") + 1);

            //날짜 폴더 생성
            String folderPath = makeFolder();

            //저장할 파일 이름 중간에 "_"를 이용해 구현
            String saveName = uploadPath + java.io.File.separator + folderPath + java.io.File.separator + "_" + saveFileName;

            Path savePath = Paths.get(saveName);

            //서버 내부 스토리지에 업로드
            Files.copy(uploadFile.getInputStream(), savePath);

            file = File.builder()
                    .originNm(originalFileName)
                    .saveNm(saveFileName)
                    .path(savePath.toString())
                    .boardId(boardId)
                    .build();

        }else{
            new IllegalAccessError("originalFileName is Null");
        }
        return file;

    }

    public Boolean delete(File info) {

        boolean result=false;
        try {
            java.io.File file = new java.io.File(info.getPath());
            result = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw (new IllegalArgumentException("서버의 파일을 삭제할 수 없습니다"));
        }
        return result;

    }

    public Resource Download(File dto) throws MalformedURLException {

        String path = dto.getPath();

        Resource resource = new UrlResource(Paths.get(path).toUri());

        return resource;
    }

    public String makeFolder() {

        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", java.io.File.separator);

        //부모 파일의 경로와 , 그 하위의 파일명을 각각 매개변수로 지정하여 해당 경로를 조합
        java.io.File uploadPathFolder = new java.io.File(uploadPath,folderPath);

        if (!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
            log.info("-----------makeFolder----------------");
            log.info("업로드 폴더가 존재하지 않음");
        }
        return folderPath;
    }*/
}

