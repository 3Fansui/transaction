package com.wu.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TradeWarNewsTest {

    @Autowired
    private VectorStore vectorStore;
    
    /**
     * 添加最新美国贸易战相关新闻到向量数据库
     */
    @Test
    public void addTradeWarNews() {
        List<Document> documents = new ArrayList<>();
        
        // 新闻1: 美国对中国加征关税的影响及欧洲的担忧
        Document doc1 = createDocument(
            "tradewar-001",
            """
            美国对中国商品征收高额关税，欧洲担忧中国产品转向欧洲市场。由于特朗普总统政策，中国面临巨大关税壁垒，
            欧洲准备迎接被转移的商品涌入。中国生产了种类繁多、价格具有竞争力的商品——包括电动汽车、消费类电子产品、
            玩具、商业级钢材等，这些商品原本大部分销往美国市场。现在，欧盟委员会主席冯德莱恩警告"间接影响"，并誓言
            密切关注中国商品流动。一个新的特别工作组将对进口商品实施监测，看是否存在倾销迹象。欧洲内部各国对如何应对
            这一局势存在分歧，一些国家如西班牙和英国更倾向与中国加强合作，而另一些国家则担忧中国产品对本国产业的冲击。
            2023年，欧洲对中国的贸易逆差接近3320亿美元（2920亿欧元）。
            """,
            "国际贸易", "美中贸易战", "2025-04-15"
        );
        
        // 新闻2: 中方与更多国家共同应对美国关税措施
        Document doc2 = createDocument(
            "tradewar-002",
            """
            中国商务部部长王文涛近期密集与各方沟通，包括与欧盟委员会贸易委员、东盟轮值主席国、二十国集团轮值主席国、
            金砖国家轮值主席国等举行会谈，讨论应对美国所谓"对等"关税问题。双方达成共识，反对美国加征关税的行为，
            愿同其他世贸组织成员一道，维护以世贸组织为核心的多边贸易体制。中欧双方同意尽快启动磋商，深入讨论市场准入
            相关问题，并立即开展电动汽车价格承诺谈判，支持重启中欧贸易救济对话机制。东盟轮值主席国马来西亚贸工部长
            扎夫鲁明确表示，美国政府有关政策不符合世界贸易组织所规定的自由公平贸易原则。越来越多的国家正在看清美国
            关税讹诈的本质，面对美方的单边霸凌做法，以开放合作和多边主义对冲单边主义、保护主义和霸凌行径，成为更多
            国家的选择。
            """,
            "国际贸易", "美中贸易战", "2025-04-13"
        );
        
        // 新闻3: 欧盟在中美贸易战中的角色与立场
        Document doc3 = createDocument(
            "tradewar-003",
            """
            随着中美贸易战升级，欧盟感觉自己在对华关系中拥有更多筹码。在法国外交部长让-诺埃尔·巴罗上个月访问北京时，
            他向中方提出了关于白兰地进口关税和免税店销售的要求，北京方面做出了积极回应，暂停了对白兰地的永久性关税
            直到7月，尽管临时关税仍将继续征收。欧洲政策制定者正在密切研究这一微小的胜利。罗迪恩研究所合伙人阿加莎·克拉茨
            表示："我认为我们拥有比以往更大的筹码，因为中国是唯一被针对的国家。我们不再被针对。"她指的是特朗普总统的
            "对等关税"，欧盟在周三获得了美国较低的关税税率。"中国面临美国125%的关税，这完全令人震惊，"她补充道。
            与此同时，欧盟正在将一些原本针对中国的贸易武器转向华盛顿，以备在盟友关系恶化的贸易战中使用。去年，当欧盟
            准备对中国制造的电动汽车征收关税时，欧盟的统一性受到了考验，现在这种统一性再次面临考验，但情况有所不同。
            """,
            "国际贸易", "欧盟立场", "2025-04-17"
        );
        
        // 新闻4: 关税对中国经济的影响及应对策略
        Document doc4 = createDocument(
            "tradewar-004",
            """
            面对美国高额关税，中国企业正在寻找多种途径应对这一挑战。美国东部时间4月10日，美方发布行政令，宣布将对
            中国输美商品加征的84%所谓"对等"关税进一步提高至125%，这对中国出口企业构成巨大压力。然而，中国企业
            已开始采取多种措施，包括开拓新市场、提升产品质量、加强技术创新等。一些中国企业选择自研新技术破局，打破
            国外垄断，极大地缓解了贸易战的影响。例如，国内一家涉足光伏行业的企业，在2018年中美贸易摩擦开始后，
            面对美国对从中国进口的光伏组件加征关税，该公司选择自研光伏材料新技术，成功打破了国外技术垄断。同时，
            中国也在积极寻求与其他国家的合作，包括欧盟、东盟等区域，以减轻对美国市场的依赖。据CNBC等美国媒体报道，
            根据美国海关的最新指南，智能手机和电脑将不受特朗普政府的"对等"关税政策的影响，显示美国自身也担忧关税
            政策对本国经济的冲击。
            """,
            "国际贸易", "中国应对", "2025-04-12"
        );
        
        documents.add(doc1);
        documents.add(doc2);
        documents.add(doc3);
        documents.add(doc4);
        
        // 向向量数据库添加文档
        vectorStore.add(documents);
        
        System.out.println("已成功添加" + documents.size() + "条美国贸易战相关新闻到向量数据库");
    }
    
    /**
     * 测试搜索贸易战相关新闻
     */
    @Test
    public void searchTradeWarNews() {
        // 构建向量搜索请求
        SearchRequest request = SearchRequest.builder()
                .query("美国对中国加征关税对欧洲有什么影响？")
                .topK(2)
                .similarityThreshold(0.6f)
                .build();
        
        // 向量数据库检索
        List<Document> results = vectorStore.similaritySearch(request);
        
        System.out.println("搜索结果数量: " + results.size());
        results.forEach(doc -> {
            System.out.println("===============================================");
            System.out.println("文档ID: " + doc.getId());
            System.out.println("相似度得分: " + doc.getScore());
            System.out.println("文档内容: " + doc.getText());
            System.out.println("文档元数据: " + doc.getMetadata());
            System.out.println("===============================================");
        });
    }
    
    /**
     * 创建文档，包含元数据
     */
    private Document createDocument(String id, String content, String category, String tags, String date) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("category", category);
        metadata.put("tags", tags);
        metadata.put("date", date);
        return new Document(id, content, metadata);
    }
} 