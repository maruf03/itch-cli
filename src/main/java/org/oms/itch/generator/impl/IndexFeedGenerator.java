package org.oms.itch.generator.impl;

import org.oms.itch.generator.ITCHMessageGenerator;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.Map;
import java.util.Random;

public class IndexFeedGenerator implements ITCHMessageGenerator {
    Random random;
    String[] messageTypes = { "T", "S", "L", "M", "P", "R", "H", "Z" }; // ITCH Index
    byte[] eventCodes = { 'O', 'S', 'Q', 'M', 'E', 'C', 'A', 'B', 'I' }; // System Event Codes

    String[][] companyDetails = { { "1234", "ACI Limited", "ACI" }, { "5678", "The City Bank Ltd.", "CITYBANK" } }; // Company
                                                                                                                    // Details:
                                                                                                                    // {Company
                                                                                                                    // Unique
                                                                                                                    // ID,
                                                                                                                    // Company
                                                                                                                    // Name,
                                                                                                                    // Company
                                                                                                                    // Ticker}

    static byte[] tradingState = { 'V', 'T' };

    static byte[] tradingStateReason = { ' ', 'I', 'i' };

    byte[] priceType = { 'U', 'P' };

    byte[] marketType = { 'S', 'P', 'I' };

    byte[] listingType = { 'A', 'B', 'N', 'Z' };

    Map<Integer, String[]> orderbookDirectory = Map.ofEntries(
            Map.entry(1234,
                    new String[] { "ISIN12345678", "SEC001", "USD", "GroupA", "50", "101", "201", "2", "20240101",
                            "1200", "123", "Finance", "InstrumentAB", "ABC Corporation" }),
            Map.entry(5678, new String[] { "ISIN98765432", "SEC002", "EUR", "GroupB", "75", "102", "202", "3",
                    "20241231", "1400", "456", "Technology", "InstrumentXY", "XYZ Corporation" }));

    public IndexFeedGenerator() {
        random = new Random();
    }

    @Override
    public ByteBuffer nextMessage() {
        String messageType = messageTypes[random.nextInt(messageTypes.length)];
        return generateMessage(messageType);
    }

    @Override
    public ByteBuffer nextMessage(String type) {
        return generateMessage(type);
    }

    private ByteBuffer generateMessage(String messageType) {
        return switch (messageType) {
            case "T" -> generateTimestamp();
            case "S" -> generateSystemEvent();
            case "L" -> generatePriceTick();
            case "M" -> generateQuantityTick();
            case "P" -> generateCompanyDirectory();
            case "R" -> generateOrderbookDirectory();
            case "H" -> generateOrderbookTradingAction();
            case "Z" -> generateIndexValue();
            default -> throw new IllegalStateException("Unexpected value: " + messageType);
        };
    }

    private ByteBuffer generateIndexValue() {
        ByteBuffer buffer = ByteBuffer.allocate(17);
        buffer.put((byte) 'Z');
        buffer.putInt(LocalTime.now().toSecondOfDay());
        buffer.putInt(1234);
        buffer.putLong(random.nextLong(10L, 100000L));

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderbookTradingAction() {
        ByteBuffer buffer = ByteBuffer.allocate(11); // Total length as per the specification

        buffer.put((byte) 'H'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of nanoseconds since last received timestamp message
        buffer.putInt(1234); // Orderbook
        buffer.put(tradingState[random.nextInt(tradingState.length)]); // Trading State
        buffer.put(tradingStateReason[random.nextInt(tradingStateReason.length)]); // Trading State Reason

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderbookDirectory() {
        ByteBuffer buffer = ByteBuffer.allocate(163); // Total length as per the specification

        Integer[] orderbookIds = orderbookDirectory.keySet().toArray(new Integer[0]);
        int orderbookId = orderbookIds[random.nextInt(orderbookIds.length)];
        String[] orderbookDetails = orderbookDirectory.get(orderbookId);

        buffer.put((byte) 'R'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putInt(orderbookId); // orderbook id
        buffer.put(priceType[random.nextInt(priceType.length)]); // priceType
        buffer.put(String.format("%-" + 12 + "s", orderbookDetails[0]).getBytes()); // ISIN right padded with spaces
        buffer.put(String.format("%-" + 12 + "s", orderbookDetails[1]).getBytes()); // Sec Code right padded with spaces
        buffer.put(String.format("%-" + 3 + "s", orderbookDetails[2]).getBytes()); // Currency right padded with spaces
        buffer.put(String.format("%-" + 8 + "s", orderbookDetails[3]).getBytes()); // Group right padded with spaces
        buffer.putLong(Long.parseLong(orderbookDetails[4])); // minimum quantity
        buffer.putInt(Integer.parseInt(orderbookDetails[5])); // quantity tick size table id
        buffer.putInt(Integer.parseInt(orderbookDetails[6])); // price tick size table id
        buffer.putInt(Integer.parseInt(orderbookDetails[7])); // price decimals
        buffer.putInt(Integer.parseInt(orderbookDetails[8])); // delisting date
        buffer.putInt(Integer.parseInt(orderbookDetails[9])); // delisting time
        buffer.put(marketType[random.nextInt(marketType.length)]); // marketType
        buffer.putInt(Integer.parseInt(orderbookDetails[10])); // CompanyId
        buffer.put(listingType[random.nextInt(listingType.length)]); // ListingType
        buffer.put(String.format("%-" + 12 + "s", orderbookDetails[11]).getBytes()); // Sector right padded with spaces
        buffer.put(String.format("%-" + 12 + "s", orderbookDetails[12]).getBytes()); // Instrument right padded with
                                                                                     // spaces
        buffer.put(String.format("%-" + 60 + "s", orderbookDetails[13]).getBytes()); // SecurityName right padded with
                                                                                     // spaces

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateCompanyDirectory() {
        ByteBuffer buffer = ByteBuffer.allocate(69); // Total length as per the specification
        String[] companyDetail = companyDetails[random.nextInt(companyDetails.length)];
        buffer.put((byte) 'P'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of nanoseconds since last received timestamp message
        buffer.putInt(Integer.parseInt(companyDetail[0])); // Company unique id
        buffer.put(String.format("%-" + 30 + "s", companyDetail[1]).getBytes()); // right padded with spaces
        buffer.put(String.format("%-" + 30 + "s", companyDetail[2]).getBytes()); // right padded with spaces

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateQuantityTick() {
        ByteBuffer buffer = ByteBuffer.allocate(25); // Total length as per the specification

        buffer.put((byte) 'M'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of nanoseconds since last received timestamp message
        buffer.putInt(1234); // Ticksize table id
        buffer.putLong(1L); // Tick Size
        buffer.putLong(1L); // Price start

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generatePriceTick() {
        ByteBuffer buffer = ByteBuffer.allocate(17); // Total length as per the specification

        buffer.put((byte) 'L'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of nanoseconds since last received timestamp message
        buffer.putInt(1234); // Ticksize table id
        buffer.putInt(10); // Tick Size
        buffer.putInt(10); // Price start

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateSystemEvent() {
        ByteBuffer buffer = ByteBuffer.allocate(18); // Total length as per the specification

        buffer.put((byte) 'S'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of nanoseconds since last received timestamp message
        buffer.put(String.format("%-" + 8 + "s", "SME").getBytes()); // right padded with spaces
        buffer.put(eventCodes[random.nextInt(eventCodes.length)]); // Event Code
        buffer.putInt(0); // Market ID

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateTimestamp() {
        ByteBuffer buffer = ByteBuffer.allocate(5); // Total length as per the specification

        buffer.put((byte) 'T'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Number of seconds since midnight

        buffer.flip();
        return buffer;
    }
}
