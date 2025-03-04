package com.example.item.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.UUID;

@Service
public class AwsS3Service {
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @SneakyThrows
    public String upload(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new Exception("파일이 누락되었거나 비어있습니다.");
        }
        return checkAndUpload(file);
    }

    private String checkAndUpload(MultipartFile file) {
        // 원본 파일명 획득
        String originalFilename = file.getOriginalFilename();

        // 파일 확장자 추출
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        // UUID를 포함하여 고유한 파일명 생성
        String s3UploadName = UUID.randomUUID().toString().substring(0, 12) + "-" + originalFilename;

        String url = "";
        try {
            // 파일 내용을 바이트 배열로 변환
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + ext);
            metadata.setContentLength(bytes.length);

            // S3에 업로드 (고유한 파일명을 사용)
            PutObjectRequest obj = new PutObjectRequest(
                    bucketName,             // 버킷 이름
                    s3UploadName,           // ✅ UUID가 포함된 고유 파일명 사용
                    byteArrayInputStream,
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead); // ✅ 퍼블릭 읽기 권한 추가

            amazonS3.putObject(obj); // 업로드 실행

            // 스트림 닫기
            byteArrayInputStream.close();

            // S3에서 퍼블릭 URL 생성 (s3UploadName을 사용해야 함)
            url = amazonS3.getUrl(bucketName, s3UploadName).toString();

        } catch (Exception e) {
            System.out.println("파일 업로드 중 오류 발생: " + e.getMessage());
        }
        return url;
    }

    public void delete(String itemFile) {
        try {
            // S3 URL에서 파일명(키) 추출
            String fileName = itemFile.substring(itemFile.lastIndexOf("/") + 1);

            // S3에서 파일 삭제
            amazonS3.deleteObject(bucketName, fileName);

            System.out.println("파일 삭제 성공: " + fileName);
        } catch (Exception e) {
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }
    }
}
