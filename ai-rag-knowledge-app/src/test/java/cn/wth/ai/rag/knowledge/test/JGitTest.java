package cn.wth.ai.rag.knowledge.test;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * @Author: 5th
 * @Description: JGit仓库测试类
 * @CreateTime: 2025-04-05 20:48
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JGitTest {

    @Resource
    private TokenTextSplitter tokenTextSplitter;
    @Resource
    private PgVectorStore pgVectorStore;

    @Test
    public void test() throws Exception {
        String repoURL = "https://github.com/110LK/ai-rag-knowledge";
        String username = "110LK";
        String password = "DqUc-jjKcNpgKaw8nmReNazm";

        String localPath = "./cloned-repo";
        log.info("克隆路径：" + new File(localPath).getAbsolutePath());

        FileUtils.deleteDirectory(new File(localPath));

        Git git = Git.cloneRepository()
                .setURI(repoURL)
                .setDirectory(new File(localPath))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .call();

        git.close();
    }

    @Test
    public void test_file() throws IOException {
        Files.walkFileTree(Paths.get("./cloned-repo"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                log.info("文件路径:{}", file.toString());

                PathResource resource = new PathResource(file);
                TikaDocumentReader reader = new TikaDocumentReader(resource);

                List<Document> documents = reader.get();
                List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

                documents.forEach(doc -> doc.getMetadata().put("knowledge", "ai-rag-knowledge"));
                documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "ai-rag-knowledge"));

                pgVectorStore.accept(documentSplitterList);

                return FileVisitResult.CONTINUE;
            }
        });
    }

}
