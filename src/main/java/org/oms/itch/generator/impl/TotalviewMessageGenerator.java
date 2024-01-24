package org.oms.itch.generator.impl;

import org.oms.itch.generator.ITCHMessageGenerator;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.Map;
import java.util.Random;

public class TotalviewMessageGenerator implements ITCHMessageGenerator {
    Random random;
    static String[] messageTypes = {"T", "S", "L", "M", "P", "R", "H", "A", "E", "C", "B", "D", "U", "I", "Q"}; // ITCH TotalView
    final static byte[] eventCodes = {'O', 'S', 'Q', 'M', 'E', 'C', 'A', 'B', 'I'}; // System Event Codes

    String[][] companyDetails = {{"1234", "ACI Limited", "ACI"}, {"5678", "The City Bank Ltd.", "CITYBANK"}};

    final static byte[] tradingState = {'V', 'T'};

    final static byte[] tradingStateReason = {' ', 'I', 'i'};

    final static byte[] priceType = {'U', 'P'};

    final static byte[] marketType = {'S', 'P', 'I'};

    final static byte[] listingType = {'A', 'B', 'N', 'Z'};

    final static byte[] orderVerbs = {'B', 'S'};

    final static byte[] printable = {'Y', 'N'};

    final static byte[] brokenTradeReason = {'S'};

    final static byte[] crossType = {'O', 'C', 'I'};

    Map<Integer, String[]> orderbookDirectory = Map.ofEntries(Map.entry(1234, new String[]{"ISIN12345678", "SEC001", "USD", "GroupA", "50", "101", "201", "2", "20240101", "1200", "123", "Finance", "InstrumentAB", "ABC Corporation"}), Map.entry(5678, new String[]{"ISIN98765432", "SEC002", "EUR", "GroupB", "75", "102", "202", "3", "20241231", "1400", "456", "Technology", "InstrumentXY", "XYZ Corporation"}));

    public TotalviewMessageGenerator() {
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
            case "A" -> generateAddOrder();
            case "E" -> generateOrderExecuted();
            case "C" -> generateOrderExecutedWithPrice();
            case "B" -> generateBrokenTrade();
            case "D" -> generateOrderDelete();
            case "U" -> generateOrderReplace();
            case "I" -> generateIndicativePrice();
            case "Q" -> generateTrade();
            default -> throw new IllegalStateException("Unexpected value: " + messageType);
        };
    }

    private ByteBuffer generateTrade() {
        ByteBuffer buffer = ByteBuffer.allocate(30); // Total length as per the specification

        buffer.put((byte) 'Q'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putLong(100000L); // executed quantity
        buffer.putInt(15000); // orderbook
        buffer.put(printable[random.nextInt(printable.length)]); // Printable
        buffer.putInt(150); // execution price
        buffer.putLong(100000L); // match number

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateIndicativePrice() {
        ByteBuffer buffer = ByteBuffer.allocate(30); // Total length as per the specification

        buffer.put((byte) 'I'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putLong(100000L); // theoretical opening quantity
        buffer.putInt(15000); // orderbook
        buffer.putInt(150); // best bid
        buffer.putInt(145); // best offer
        buffer.putInt(15000); // theoretical opening price
        buffer.put(crossType[random.nextInt(crossType.length)]); // Cross Type

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderReplace() {
        ByteBuffer buffer = ByteBuffer.allocate(33); // Total length as per the specification

        buffer.put((byte) 'U'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putLong(987654321L); // original Order Number
        buffer.putLong(987654322L); // new Order Number
        buffer.putLong(1000L); // Quantity
        buffer.putInt(15000); // Price

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderDelete() {
        ByteBuffer buffer = ByteBuffer.allocate(13); // Total length as per the specification

        buffer.put((byte) 'D'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp - Nanoseconds
        buffer.putLong(987654321L); // Order Reference Number

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateBrokenTrade() {
        ByteBuffer buffer = ByteBuffer.allocate(14); // Total length as per the specification

        buffer.put((byte) 'B'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putLong(987654321L); // Match Number
        buffer.put(brokenTradeReason[random.nextInt(brokenTradeReason.length)]); // Order Verb (Buy)

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderExecutedWithPrice() {
        ByteBuffer buffer = ByteBuffer.allocate(34); // Total length as per the specification

        buffer.put((byte) 'C'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp - Nanoseconds
        buffer.putLong(987654321L); // Order Reference Number
        buffer.putLong(50); // Executed Shares
        buffer.putLong(11223344L); // Match Number
        buffer.put(printable[random.nextInt(printable.length)]); // Printable
        buffer.putInt(15000); // Printable Price

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateOrderExecuted() {
        ByteBuffer buffer = ByteBuffer.allocate(29); // Total length as per the specification

        buffer.put((byte) 'E'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp - Nanoseconds
        buffer.putLong(987654321L); // Order Reference Number
        buffer.putLong(50); // Executed Shares
        buffer.putLong(11223344L); // Match Number

        buffer.flip();
        return buffer;
    }

    private ByteBuffer generateAddOrder() {
        ByteBuffer buffer = ByteBuffer.allocate(30); // Total length as per the specification

        buffer.put((byte) 'A'); // Message Type
        buffer.putInt(LocalTime.now().toSecondOfDay()); // Timestamp
        buffer.putLong(987654321L); // Order Number
        buffer.put(orderVerbs[random.nextInt(orderVerbs.length)]); // Order Verb (Buy)
        buffer.putLong(1000L); // Quantity
        buffer.putInt(1234); // Orderbook
        buffer.putInt(Integer.decode("0x7FFFFFFF")); // Price

        buffer.flip();
        return buffer;
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
