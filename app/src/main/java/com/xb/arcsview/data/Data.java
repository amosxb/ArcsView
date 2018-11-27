package com.xb.arcsview.data;

public class Data {

    /**
     * 金额
     */
    private double amount;
    /**
     * 金额内容
     */
    private String amountText;

    /**
     * 颜色值
     */
    private int color;

    /**
     * 角度值
     */
    private float angle;

    /**
     * 起始角度值
     */
    private float currentStartAngle;

    private float percentage;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountText() {
        return amountText;
    }

    public void setAmountText(String amountText) {
        this.amountText = amountText;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getCurrentStartAngle() {
        return currentStartAngle;
    }

    public void setCurrentStartAngle(float currentStartAngle) {
        this.currentStartAngle = currentStartAngle;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Data{" +
                "amount=" + amount +
                ", amountText='" + amountText + '\'' +
                ", color=" + color +
                ", angle=" + angle +
                ", currentStartAngle=" + currentStartAngle +
                '}';
    }
}
