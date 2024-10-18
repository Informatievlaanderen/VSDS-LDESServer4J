package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "retention_member_views")
public class MemberViewsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private MemberPropertiesEntity member;

	@Column(name = "view")
	private String view;

	protected MemberViewsEntity() {}
	public MemberViewsEntity(MemberPropertiesEntity member, String view) {
		this.member = member;
		this.view = view;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MemberPropertiesEntity getMember() {
		return member;
	}

	public void setMember(MemberPropertiesEntity member) {
		this.member = member;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
}
