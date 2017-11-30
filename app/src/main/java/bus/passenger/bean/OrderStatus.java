package bus.passenger.bean;

/**
 * 订单状态信息，轮询获取
 */
public class OrderStatus {
	
	/**订单id**/
	private String orderUuid;
	
	/**司机电话**/
	private String driverMobile;

	/** 订单主状态1：订单初识化,2订单进行中3：订单结束（待支付）4：支付完成5.取消 */
	private int mainStatus;

	/** 订单子状态(100.等待应答（拼车中） 200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达 210.出发接乘客 220.司机到达等待乘客 300.行程开始 301到达目的地400.待支付 500.已完成(待评价) 501.已完成-已评价 600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消) */
	private int subStatus;
	
    /** 纬度 */
    private double lat;
    
    /** 经度 */
    private double lng;
    
    /** 订单费用 */
    private double totalFare;

	public String getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(String orderUuid) {
		this.orderUuid = orderUuid;
	}

	public String getDriverMobile() {
		return driverMobile;
	}

	public void setDriverMobile(String driverMobile) {
		this.driverMobile = driverMobile;
	}

	public int getMainStatus() {
		return mainStatus;
	}

	public void setMainStatus(int mainStatus) {
		this.mainStatus = mainStatus;
	}

	public int getSubStatus() {
		return subStatus;
	}

	public void setSubStatus(int subStatus) {
		this.subStatus = subStatus;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(double totalFare) {
		this.totalFare = totalFare;
	}

	@Override
	public String toString() {
		return "OrderStatus{" +
				"orderUuid='" + orderUuid + '\'' +
				", driverMobile='" + driverMobile + '\'' +
				", mainStatus=" + mainStatus +
				", subStatus=" + subStatus +
				", lat=" + lat +
				", lng=" + lng +
				", totalFare=" + totalFare +
				'}';
	}
}
