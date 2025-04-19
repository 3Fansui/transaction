package com.wu.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VectorStoreTest {

    @Autowired
    private VectorStore vectorStore;

    /**
     * 测试向量数据库的添加功能
     */
    @Test
    public void testAdd() {
        // 1. 准备测试数据
        List<Document> documents = new ArrayList<>();
        
        // 添加商品描述作为测试数据
        Document doc1 = new Document("华为 Mate 60 Pro 6.8英寸屏幕，麒麟9000S芯片，卫星通信，IP68防水，长续航", createMetadata("p001", "手机"));
        Document doc2 = new Document("苹果 iPhone 15 Pro Max 6.7英寸屏幕，A17 Pro芯片，USB-C接口，钛金属边框", createMetadata("p002", "手机"));
        Document doc3 = new Document("小米14 Ultra 6.73英寸屏幕，徕卡光学镜头，高通骁龙8 Gen 3芯片，67W快充", createMetadata("p003", "手机"));
        
        documents.add(doc1);
        documents.add(doc2);
        documents.add(doc3);
        
        // 2. 存储文档到向量数据库
        try {
            vectorStore.add(documents);
            System.out.println("成功存储" + documents.size() + "条文档到向量数据库");
        } catch (Exception e) {
            fail("存储文档到向量数据库失败: " + e.getMessage());
        }
        
        // 3. 验证是否添加成功（通过查询验证）
        List<Document> results = vectorStore.similaritySearch("华为手机");
        assertFalse(results.isEmpty(), "应该能找到与'华为手机'相关的文档");
        
        System.out.println("查询结果:");
        for (Document doc : results) {
            System.out.println("- 相关度: " + doc.getMetadata().get("similarity") + 
                              ", 产品ID: " + doc.getMetadata().get("productId") + 
                              ", 内容: " + doc.getText());
        }
    }

    /**
     * 测试向量数据库的查询功能
     */
    @Test
    public void testSearch() {
        // 1. 准备测试数据并添加
        List<Document> documents = new ArrayList<>();
        
        documents.add(new Document("华为 Mate 60 Pro 6.8英寸屏幕，麒麟9000S芯片，卫星通信，IP68防水，长续航", createMetadata("p001", "手机")));
        documents.add(new Document("苹果 iPhone 15 Pro Max 6.7英寸屏幕，A17 Pro芯片，USB-C接口，钛金属边框", createMetadata("p002", "手机")));
        documents.add(new Document("小米14 Ultra 6.73英寸屏幕，徕卡光学镜头，高通骁龙8 Gen 3芯片，67W快充", createMetadata("p003", "手机")));
        documents.add(new Document("华硕 ROG 玩家国度 游戏笔记本电脑，RTX 4090显卡，13代酷睿i9处理器", createMetadata("p004", "笔记本电脑")));
        documents.add(new Document("戴尔 XPS 17 17英寸4K屏幕，高性能轻薄笔记本，长达12小时续航", createMetadata("p005", "笔记本电脑")));
        
        vectorStore.add(documents);
        
        // 2. 测试不同的查询
        System.out.println("\n=== 测试相似性检索 ===");
        
        // 2.1 测试查询"华为手机"相关内容
        testSimpleSearch("华为手机");
        
        // 2.2 测试查询"轻薄笔记本"相关内容
        testSimpleSearch("轻薄笔记本");
        
        // 2.3 测试查询"性能最好的手机"相关内容
        testSimpleSearch("性能最好的手机");
    }
    
    /**
     * 测试向量数据库的删除功能
     */
    @Test
    public void testDelete() {
        // 1. 准备测试数据
        Document testDoc = new Document("测试删除功能的临时文档", createMetadata("test_delete", "测试"));
        List<Document> documents = List.of(testDoc);
        
        // 2. 存储文档
        vectorStore.add(documents);
        
        // 3. 查询确认文档已存储
        List<Document> beforeResults = vectorStore.similaritySearch("测试删除");
        assertFalse(beforeResults.isEmpty(), "应该能找到测试文档");
        
        // 4. 如果找到文档，测试删除功能
        if (!beforeResults.isEmpty()) {
            String docId = beforeResults.get(0).getId();
            vectorStore.delete(List.of(docId));
            
            // 5. 再次查询验证是否已删除
            List<Document> afterResults = vectorStore.similaritySearch("测试删除");
            boolean foundAfterDelete = afterResults.stream()
                    .anyMatch(doc -> doc.getId().equals(docId));
            
            assertFalse(foundAfterDelete, "文档应该已被删除");
            System.out.println("成功验证删除功能");
        } else {
            System.out.println("未找到测试文档，跳过删除测试");
        }
    }
    
    /**
     * 创建文档元数据
     */
    private Map<String, Object> createMetadata(String productId, String category) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productId", productId);
        metadata.put("category", category);
        return metadata;
    }
    
    /**
     * 使用简单的similaritySearch方法测试搜索
     */
    private void testSimpleSearch(String query) {
        System.out.println("\n查询: " + query);
        
        List<Document> results = vectorStore.similaritySearch(query);
        
        assertNotNull(results, "搜索结果不应为null");
        
        System.out.println("找到 " + results.size() + " 条相关结果:");
        for (Document doc : results) {
            System.out.println("- 相关度: " + doc.getMetadata().get("similarity") + 
                              ", 产品ID: " + doc.getMetadata().get("productId") + 
                              ", 内容: " + doc.getText());
        }
    }
} 