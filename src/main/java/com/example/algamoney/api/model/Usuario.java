package com.example.algamoney.api.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

	@Entity
	@Table(name = "usuario")
	public class Usuario {/* o lado dominante é sempre o que possui o mapeamento ManyToMany, afinal é apenas a partir dele que podemos ter acesso ao mapeamento.*/

		@Id
		private Long codigo;

		private String nome;
		private String email;
		private String senha;

		@ManyToMany(fetch = FetchType.EAGER) // A entidade Usuario possui várias entidades Permissao bem como Permissao possui varios Usuario
		@JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "codigo_usuario")
			, inverseJoinColumns = @JoinColumn(name = "codigo_permissao")) //Unidirecional {codigo_permissao é o lado dominado}
		private List<Permissao> permissoes;

		public Long getCodigo() {
			return codigo;
		}

		public void setCodigo(Long codigo) {
			this.codigo = codigo;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getSenha() {
			return senha;
		}

		public void setSenha(String senha) {
			this.senha = senha;
		}

		public List<Permissao> getPermissoes() {
			return permissoes;
		}

		public void setPermissoes(List<Permissao> permissoes) {
			this.permissoes = permissoes;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Usuario other = (Usuario) obj;
			if (codigo == null) {
				if (other.codigo != null)
					return false;
			} else if (!codigo.equals(other.codigo))
				return false;
			return true;
		}

	}


