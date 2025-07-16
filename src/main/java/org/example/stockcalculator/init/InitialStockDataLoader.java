package org.example.stockcalculator.init;

import java.util.List;
import java.util.Set;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialStockDataLoader {

    private final StockRepository stockRepository;

    @PostConstruct
    public void insertDefaultStocks() {
        List<Stock> defaultStocks = List.of(
                createStock("AAPL", "Apple Inc.", "Apple Inc. is a leading technology company renowned for its innovative products such as the iPhone, iPad, and Mac computers. The company also offers a range of services including the App Store, Apple Music, and iCloud. Apple is known for its strong brand loyalty and design-driven approach. It is headquartered in Cupertino, California, and is one of the world's most valuable companies."),
                createStock("MSFT", "Microsoft Corporation", "Microsoft Corporation is a global technology giant best known for its Windows operating system and Office productivity suite. The company is a major player in cloud computing through its Azure platform and has a significant presence in gaming with Xbox. Microsoft also develops enterprise software and services. Its headquarters are in Redmond, Washington."),
                createStock("GOOGL", "Alphabet Inc.", "Alphabet Inc. is the parent company of Google, the world's leading search engine. The company is a dominant force in online advertising, cloud computing, and mobile operating systems with Android. Alphabet also invests in various innovative projects such as self-driving cars and health technology. Its headquarters are located in Mountain View, California."),
                createStock("AMZN", "Amazon.com Inc.", "Amazon.com Inc. is a multinational technology company primarily focused on e-commerce, cloud computing, and digital streaming. It operates the world's largest online marketplace and is a leader in cloud infrastructure through Amazon Web Services (AWS). Amazon has expanded into entertainment, logistics, and artificial intelligence. The company is headquartered in Seattle, Washington."),
                createStock("NVDA", "NVIDIA Corporation", "NVIDIA Corporation is a leading designer of graphics processing units (GPUs) for gaming, professional visualization, and artificial intelligence. The company has become a key player in the AI and data center markets. NVIDIA's technology powers everything from gaming PCs to autonomous vehicles. Its headquarters are in Santa Clara, California."),
                createStock("META", "Meta Platforms Inc.", "Meta Platforms Inc., formerly Facebook, is a global social media and technology company. It owns and operates platforms such as Facebook, Instagram, and WhatsApp, connecting billions of users worldwide. Meta is investing heavily in virtual and augmented reality to build the metaverse. The company is based in Menlo Park, California."),
                createStock("TSLA", "Tesla Inc.", "Tesla Inc. is an electric vehicle and clean energy company founded by Elon Musk. It designs and manufactures electric cars, battery energy storage, and solar products. Tesla is known for its innovation in autonomous driving and sustainable energy solutions. The company is headquartered in Palo Alto, California."),
                createStock("BRK.A", "Berkshire Hathaway Inc.", "Berkshire Hathaway Inc. is a multinational conglomerate holding company led by Warren Buffett. It owns a diverse range of businesses including insurance, utilities, railroads, and consumer products. The company is also known for its significant equity investments in public companies. Berkshire Hathaway is based in Omaha, Nebraska."),
                createStock("V", "Visa Inc.", "Visa Inc. is a global payments technology company that facilitates digital fund transfers among consumers, merchants, and financial institutions. It operates one of the world's largest electronic payment networks. Visa is a leader in innovation for secure and convenient payment solutions. The company is headquartered in Foster City, California."),
                createStock("JPM", "JPMorgan Chase & Co.", "JPMorgan Chase & Co. is a leading global financial services firm offering investment banking, asset management, and consumer banking. It is the largest bank in the United States by assets. The company serves millions of customers and businesses worldwide. Its headquarters are in New York City."),
                createStock("WMT", "Walmart Inc.", "Walmart Inc. is the world's largest retailer, operating a chain of hypermarkets, discount department stores, and grocery stores. The company is known for its low prices and extensive global supply chain. Walmart also has a growing e-commerce presence. It is headquartered in Bentonville, Arkansas."),
                createStock("JNJ", "Johnson & Johnson", "Johnson & Johnson is a multinational corporation specializing in pharmaceuticals, medical devices, and consumer health products. The company is known for its commitment to healthcare innovation and quality. Its products are sold in over 175 countries. Johnson & Johnson is based in New Brunswick, New Jersey."),
                createStock("UNH", "UnitedHealth Group Incorporated", "UnitedHealth Group is a diversified healthcare and insurance company. It provides health care coverage, benefits, and services through its UnitedHealthcare and Optum subsidiaries. The company is a leader in health technology and data analytics. UnitedHealth Group is headquartered in Minnetonka, Minnesota."),
                createStock("MA", "Mastercard Incorporated", "Mastercard Incorporated is a global payments and technology company connecting consumers, financial institutions, and merchants. It provides transaction processing and payment solutions in over 210 countries. Mastercard is known for its innovation in digital payments and security. The company is based in Purchase, New York."),
                createStock("XOM", "Exxon Mobil Corporation", "Exxon Mobil Corporation is one of the world's largest publicly traded oil and gas companies. It is involved in the exploration, production, and distribution of petroleum and petrochemical products. ExxonMobil is also investing in low-carbon energy solutions. The company is headquartered in Irving, Texas."),
                createStock("PG", "Procter & Gamble Co.", "Procter & Gamble Co. is a multinational consumer goods corporation with a wide portfolio of trusted brands. Its products include personal care, cleaning agents, and health care items. P&G is known for its innovation and global reach. The company is based in Cincinnati, Ohio."),
                createStock("HD", "The Home Depot, Inc.", "The Home Depot, Inc. is the largest home improvement retailer in the United States. It supplies tools, construction products, and services to DIY customers and professionals. The company is recognized for its extensive product selection and customer service. Home Depot is headquartered in Atlanta, Georgia."),
                createStock("KO", "The Coca-Cola Company", "The Coca-Cola Company is a leading beverage corporation best known for its flagship soft drink, Coca-Cola. It owns a diverse portfolio of beverage brands sold worldwide. The company is committed to sustainability and community initiatives. Coca-Cola is based in Atlanta, Georgia."),
                createStock("PEP", "PepsiCo, Inc.", "PepsiCo, Inc. is a global food and beverage company with a broad range of popular brands. Its products include soft drinks, snacks, and convenient foods. PepsiCo is known for its innovation and strong global distribution network. The company is headquartered in Purchase, New York."),
                createStock("DIS", "The Walt Disney Company", "The Walt Disney Company is a diversified entertainment and media conglomerate. It operates film studios, theme parks, and streaming services such as Disney+. Disney is known for its iconic characters and storytelling. The company is headquartered in Burbank, California."),
                createStock("U", "Unity Software Inc.", "Unity Software Inc. is a leading platform for creating and operating interactive, real-time 3D content. The company is widely used in game development, simulations, and virtual reality applications. Unity's technology powers a significant portion of the world's video games. It is headquartered in San Francisco, California."),
                createStock("PLTR", "Palantir Technologies Inc.", "Palantir Technologies Inc. is a public American software company that specializes in big data analytics. Its platforms are used by governments and commercial clients for data integration, analysis, and security. Palantir is known for its work in defense, intelligence, and enterprise data solutions. The company is based in Denver, Colorado."),
                createStock("AMC", "AMC Entertainment Holdings Inc.", "AMC Entertainment Holdings Inc. is one of the largest movie theater chains in the world. The company operates theaters across the United States and internationally, offering a wide range of cinematic experiences. AMC is known for its innovation in moviegoing, including premium formats and loyalty programs. It is headquartered in Leawood, Kansas.")
        );
        Set<String> existingSymbols = stockRepository.findAllSymbols();
        List<Stock> stocksToAdd = defaultStocks.stream()
                .filter(stock -> !existingSymbols.contains(stock.getSymbol()))
                .toList();
        if (!stocksToAdd.isEmpty()) {
            stockRepository.saveAll(stocksToAdd);
        }
    }
    private Stock createStock(String symbol, String name, String description) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setName(name);
        stock.setDescription(description);
        return stock;
    }
}
