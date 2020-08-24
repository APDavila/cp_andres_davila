package com.componente_practico.gacela.modelo;

import java.util.Date;

public class OrdenGacela {
	private String status;
	private Results results;

	public OrdenGacela(String status, Results results) {
		super();
		this.status = status;
		this.results = results;
	}

	public OrdenGacela() {
		super();
	}

	final public class Results {
		private int id;
		private StatusOrders[] status_orders;
		private Driver driver;
		private Store store;
		private Customer customer;

		public Results(int id, StatusOrders[] status_orders, Driver driver, Store store, Customer customer) {
			super();
			this.id = id;
			this.status_orders = status_orders;
			this.driver = driver;
			this.store = store;
			this.customer = customer;
		}

		public Results() {
			super();
		}

		final public class StatusOrders {
			private String name;
			private Date created_at;
			private Double longs;
			private Double lat;

			public StatusOrders(String name, Date created_at, Double longs, Double lat) {
				super();
				this.name = name;
				this.created_at = created_at;
				this.longs = longs;
				this.lat = lat;
			}

			public StatusOrders() {
				super();
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Date getCreated_at() {
				return created_at;
			}

			public void setCreated_at(Date created_at) {
				this.created_at = created_at;
			}

			public Double getLongs() {
				return longs;
			}

			public void setLongs(Double longs) {
				this.longs = longs;
			}

			public Double getLat() {
				return lat;
			}

			public void setLat(Double lat) {
				this.lat = lat;
			}

		}

		final public class Driver {
			private String name;
			private String phone;
			private Double latitude;
			private Double longitude;
			private String plate;

			public Driver(String name, String phone, Double latitude, Double longitude, String plate) {
				super();
				this.name = name;
				this.phone = phone;
				this.latitude = latitude;
				this.longitude = longitude;
				this.plate = plate;
			}

			public Driver() {
				super();
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}

			public Double getLatitude() {
				return latitude;
			}

			public void setLatitude(Double latitude) {
				this.latitude = latitude;
			}

			public Double getLongitude() {
				return longitude;
			}

			public void setLongitude(Double longitude) {
				this.longitude = longitude;
			}

			public String getPlate() {
				return plate;
			}

			public void setPlate(String plate) {
				this.plate = plate;
			}

		}

		final public class Store {
			private String name;
			private Double latitude;
			private Double longitude;
			private int sector_id;
			private Company company;

			final public class Company {
				private String name;

				public Company(String name) {
					super();
					this.name = name;
				}

				public Company() {
					super();
				}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Double getLatitude() {
				return latitude;
			}

			public void setLatitude(Double latitude) {
				this.latitude = latitude;
			}

			public Double getLongitude() {
				return longitude;
			}

			public void setLongitude(Double longitude) {
				this.longitude = longitude;
			}

			public int getSector_id() {
				return sector_id;
			}

			public void setSector_id(int sector_id) {
				this.sector_id = sector_id;
			}

			public Company getCompany() {
				return company;
			}

			public void setCompany(Company company) {
				this.company = company;
			}

		}

		final public class Customer {
			private String name;
			private String lastname;
			private String phone;
			private Double latitude;
			private Double longitude;

			public Customer(String name, String lastname, String phone, Double latitude, Double longitude) {
				super();
				this.name = name;
				this.lastname = lastname;
				this.phone = phone;
				this.latitude = latitude;
				this.longitude = longitude;
			}

			public Customer() {
				super();
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getLastname() {
				return lastname;
			}

			public void setLastname(String lastname) {
				this.lastname = lastname;
			}

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}

			public Double getLatitude() {
				return latitude;
			}

			public void setLatitude(Double latitude) {
				this.latitude = latitude;
			}

			public Double getLongitude() {
				return longitude;
			}

			public void setLongitude(Double longitude) {
				this.longitude = longitude;
			}

		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public StatusOrders[] getStatus_orders() {
			return status_orders;
		}

		public void setStatus_orders(StatusOrders[] status_orders) {
			this.status_orders = status_orders;
		}

		public Driver getDriver() {
			return driver;
		}

		public void setDriver(Driver driver) {
			this.driver = driver;
		}

		public Store getStore() {
			return store;
		}

		public void setStore(Store store) {
			this.store = store;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}
}