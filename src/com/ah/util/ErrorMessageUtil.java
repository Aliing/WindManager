package com.ah.util;

public class ErrorMessageUtil {

	private static final String MSG_ID_SPECIFICATOR = "(###";
	public static String convertErrorCodeToMessage(int errorCode, String msgFromAp) {
		String errorMessage = "";
		
		switch (errorCode) {
		/* error from Kerberos */
		//case 0 : errorMessage = MgrUtil.getUserMessage("kdc.err.none");break;
		case 1 : errorMessage = MgrUtil.getUserMessage("kdc.err.name.exp");break;
		case 2 : errorMessage = MgrUtil.getUserMessage("kdc.err.service.exp");break;
		case 3 : errorMessage = MgrUtil.getUserMessage("kdc.err.bad.pvno");break;
		case 4 : errorMessage = MgrUtil.getUserMessage("kdc.err.c.old.mast.kvno");break;
		case 5 : errorMessage = MgrUtil.getUserMessage("kdc.err.s.old.mast.kvno");break;
		case 6 : errorMessage = MgrUtil.getUserMessage("kdc.err.c.principal.unknown");break;
		case 7 : errorMessage = MgrUtil.getUserMessage("kdc.err.s.principal.unknown");break;
		case 8 : errorMessage = MgrUtil.getUserMessage("kdc.err.principal.not.unique");break;
		case 9 : errorMessage = MgrUtil.getUserMessage("kdc.err.null.key");break;
		case 10 : errorMessage = MgrUtil.getUserMessage("kdc.err.cannot.postdate");break;
		case 11 : errorMessage = MgrUtil.getUserMessage("kdc.err.never.valid");break;
		case 12 : errorMessage = MgrUtil.getUserMessage("kdc.err.policy");break;
		case 13 : errorMessage = MgrUtil.getUserMessage("kdc.err.badoption");break;
		case 14 : errorMessage = MgrUtil.getUserMessage("kdc.err.enctype.nosupp");break;
		case 15 : errorMessage = MgrUtil.getUserMessage("kdc.err.sumtype.nosupp");break;
		case 16 : errorMessage = MgrUtil.getUserMessage("kdc.err.padata.type.nosupp");break;
		case 17 : errorMessage = MgrUtil.getUserMessage("kdc.err.trtype.nosupp");break;
		case 18 : errorMessage = MgrUtil.getUserMessage("kdc.err.client.revoked");break;
		case 19 : errorMessage = MgrUtil.getUserMessage("kdc.err.service.revoked");break;
		case 20 : errorMessage = MgrUtil.getUserMessage("kdc.err.tgt.revoked");break;
		case 21 : errorMessage = MgrUtil.getUserMessage("kdc.err.client.notyet");break;
		case 22 : errorMessage = MgrUtil.getUserMessage("kdc.err.service.notyet");break;
		case 23 : errorMessage = MgrUtil.getUserMessage("kdc.err.key.exp");break;
		case 24 : errorMessage = MgrUtil.getUserMessage("kdc.err.preauth.failed");break;
		case 25 : errorMessage = MgrUtil.getUserMessage("kdc.err.preauth.required");break;
		case 26 : errorMessage = MgrUtil.getUserMessage("kdc.err.server.nomatch");break;
		case 31 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.bad.integrity");break;
		case 32 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.tkt.expired");break;
		case 33 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.tkt.nyv");break;
		case 34 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.repeat");break;
		case 35 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.not.us");break;
		case 36 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badmatch");break;
		case 37 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.skew");break;
		case 38 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badaddr");break;
		case 39 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badversion");break;
		case 40 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.msg.type");break;
		case 41 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.modified");break;
		case 42 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badorder");break;
		case 44 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badkeyver");break;
		case 45 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.nokey");break;
		case 46 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.mut.fail");break;
		case 47 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.baddirection");break;
		case 48 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.method");break;
		case 49 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.badseq");break;
		case 50 : errorMessage = MgrUtil.getUserMessage("krb.ap.err.inapp.cksum");break;
		case 51 : errorMessage = MgrUtil.getUserMessage("krb.ap.path.not.accepted");break;
		case 52 : errorMessage = MgrUtil.getUserMessage("krb.err.response.too.bi");break;
		case 60 : errorMessage = MgrUtil.getUserMessage("krb.err.generic");break;
		case 61 : errorMessage = MgrUtil.getUserMessage("krb.err.field.toolong");break;
		/* SAMBA Error */
		case 0x0000 : errorMessage = MgrUtil.getUserMessage("nt.status.ok");break;
		case 0xC0000001 : errorMessage = MgrUtil.getUserMessage("nt.status.unsuccessful");break;
		case 0xC0000022 : errorMessage = MgrUtil.getUserMessage("nt.status.access.denied");break;
		case 0xC0000234 : errorMessage = MgrUtil.getUserMessage("nt.status.account.locked.out");break;
		case 0xC0000224 : errorMessage = MgrUtil.getUserMessage("nt.status.password.must.change");break;
		case 0xC000025a : errorMessage = MgrUtil.getUserMessage("nt.status.pwd.too.short");break;
		case 0xC000025b : errorMessage = MgrUtil.getUserMessage("nt.status.pwd.too.recent");break;
		case 0xC000025c : errorMessage = MgrUtil.getUserMessage("nt.status.pwd.history.conflict");break;
		case 0xC000005e : errorMessage = MgrUtil.getUserMessage("nt.status.no.logon.servers");break;
		case 0xC0000062 : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.account.name");break;
		case 0xC0000063 : errorMessage = MgrUtil.getUserMessage("nt.status.user.exists");break;
		case 0xC0000064 : errorMessage = MgrUtil.getUserMessage("nt.status.no.such.user");break;
		case 0xC0000065 : errorMessage = MgrUtil.getUserMessage("nt.status.group.exists");break;
		case 0xC0000066 : errorMessage = MgrUtil.getUserMessage("nt.status.no.such.group");break;
		case 0xC0000068 : errorMessage = MgrUtil.getUserMessage("nt.status.member.not.in.group");break;
		case 0xC000006a : errorMessage = MgrUtil.getUserMessage("nt.status.wrong.password");break;
		case 0xC000006b : errorMessage = MgrUtil.getUserMessage("nt.status.ill.formed.password");break;
		case 0xC000006c : errorMessage = MgrUtil.getUserMessage("nt.status.password.restriction");break;
		case 0xC000006d : errorMessage = MgrUtil.getUserMessage("nt.status.logon.failure");break;
		case 0xC000006e : errorMessage = MgrUtil.getUserMessage("nt.status.account.restriction");break;
		case 0xC000006f : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.logon.hours");break;
		case 0xC0000070 : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.workstation");break;
		case 0xC0000071 : errorMessage = MgrUtil.getUserMessage("nt.status.password.expired");break;
		case 0xC0000072 : errorMessage = MgrUtil.getUserMessage("nt.status.account.disabled");break;
		case 0xC0000017 : errorMessage = MgrUtil.getUserMessage("nt.status.no.memory");break;
		case 0xC0000233 : errorMessage = MgrUtil.getUserMessage("nt.status.domain.controller.not.found");break;
		case 0xC00000ac : errorMessage = MgrUtil.getUserMessage("nt.status.pipe.not.available");break;
		case 0xC0000002 : errorMessage = MgrUtil.getUserMessage("nt.status.not.implemented");break;
		case 0xC0000003 : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.info.class");break;
		case 0xC0000004 : errorMessage = MgrUtil.getUserMessage("nt.status.info.length.mismatch");break;
		case 0xC0000005 : errorMessage = MgrUtil.getUserMessage("nt.status.access.violation");break;
		case 0xC0000008 : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.handle");break;
		case 0xC000000d : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.parameter");break;
		//case 0xC0000017 : errorMessage = MgrUtil.getUserMessage("nt.status.no.memory");break;
		case 0xC0000023 : errorMessage = MgrUtil.getUserMessage("nt.status.buffer.too.small");break;
		case 0xC0000059 : errorMessage = MgrUtil.getUserMessage("nt.status.revision.mismatch");break;
		case 0xC000005f : errorMessage = MgrUtil.getUserMessage("nt.status.no.such.logon.session");break;
		case 0xC0000060 : errorMessage = MgrUtil.getUserMessage("nt.status.no.such.privilege");break;
		case 0xC000007a : errorMessage = MgrUtil.getUserMessage("nt.status.procedure.not.found");break;
		case 0xC0000080 : errorMessage = MgrUtil.getUserMessage("nt.status.server.disabled");break;
		case 0xC00000ad : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.pipe.state");break;
		case 0xC00000ae : errorMessage = MgrUtil.getUserMessage("nt.status.pipe.busy");break;
		case 0xC00000af : errorMessage = MgrUtil.getUserMessage("nt.status.illegal.function");break;
		case 0xC00000b0 : errorMessage = MgrUtil.getUserMessage("nt.status.pipe.disconnected");break;
		case 0xC00000b1 : errorMessage = MgrUtil.getUserMessage("nt.status.pipe.closing");break;
		case 0xC00000bc : errorMessage = MgrUtil.getUserMessage("nt.status.remote.not.listening");break;
		case 0xC00000bd : errorMessage = MgrUtil.getUserMessage("nt.status.duplicate.name");break;
		case 0xC00000c6 : errorMessage = MgrUtil.getUserMessage("nt.status.print.queue.full");break;
		case 0xC00000c7 : errorMessage = MgrUtil.getUserMessage("nt.status.no.spool.space");break;
		case 0xC00000cc : errorMessage = MgrUtil.getUserMessage("nt.status.bad.network.name");break;
		case 0xC0000236 : errorMessage = MgrUtil.getUserMessage("nt.status.connection.refused");break;
		case 0xC00000cd : errorMessage = MgrUtil.getUserMessage("nt.status.too.many.names");break;
		case 0xC00000ce : errorMessage = MgrUtil.getUserMessage("nt.status.too.many.sessions");break;
		case 0xC00000dc : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.server.state");break;
		case 0xC00000dd : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.domain.state");break;
		case 0xC00000de : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.domain.role");break;
		case 0xC00000df : errorMessage = MgrUtil.getUserMessage("nt.status.no.such.domain");break;
		case 0xC00000e0 : errorMessage = MgrUtil.getUserMessage("nt.status.domain.exists");break;
		case 0xC00000e1 : errorMessage = MgrUtil.getUserMessage("nt.status.domain.limit.exceeded");break;
		case 0xC0000104 : errorMessage = MgrUtil.getUserMessage("nt.status.bad.logon.session.state");break;
		case 0xC0000105 : errorMessage = MgrUtil.getUserMessage("nt.status.logon.session.collision");break;
		case 0xC000010b : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.logon.type");break;
		case 0xC0000120 : errorMessage = MgrUtil.getUserMessage("nt.status.cancelled");break;
		case 0xC0000122 : errorMessage = MgrUtil.getUserMessage("nt.status.invalid.computer.name");break;
		case 0xC0000132 : errorMessage = MgrUtil.getUserMessage("nt.status.logon.server.conflict");break;
		case 0xC0000133 : errorMessage = MgrUtil.getUserMessage("nt.status.time.difference.at.dc");break;
		case 0xC000014b : errorMessage = MgrUtil.getUserMessage("nt.status.pipe.broken");break;
		case 0xC000014c : errorMessage = MgrUtil.getUserMessage("nt.status.registry.corrupt");break;
		case 0xC0000156 : errorMessage = MgrUtil.getUserMessage("nt.status.too.many.secrets");break;
		case 0xC000017e : errorMessage = MgrUtil.getUserMessage("nt.status.too.many.sids");break;
		case 0xC000017f : errorMessage = MgrUtil.getUserMessage("nt.status.lm.cross.encryption.required");break;
		case 0xC0000188 : errorMessage = MgrUtil.getUserMessage("nt.status.log.file.full");break;
		case 0xC000018a : errorMessage = MgrUtil.getUserMessage("nt.status.no.trust.lsa.secret");break;
		case 0xC000018b : errorMessage = MgrUtil.getUserMessage("nt.status.no.trust.sam.account");break;
		case 0xC000018c : errorMessage = MgrUtil.getUserMessage("nt.status.trusted.domain.failure");break;
		case 0xC000018d : errorMessage = MgrUtil.getUserMessage("nt.status.trusted.relationship.failure");break;
		case 0xC0000190 : errorMessage = MgrUtil.getUserMessage("nt.status.trust.failure");break;
		case 0xC0000192 : errorMessage = MgrUtil.getUserMessage("nt.status.netlogon.not.started");break;
		case 0xC0000193 : errorMessage = MgrUtil.getUserMessage("nt.status.account.expired");break;
		case 0xC0000195 : errorMessage = MgrUtil.getUserMessage("nt.status.network.credential.conflict");break;
		case 0xC0000196 : errorMessage = MgrUtil.getUserMessage("nt.status.remote.session.limit");break;
		case 0xC0000198 : errorMessage = MgrUtil.getUserMessage("nt.status.nologon.interdomain.trust.account");break;
		case 0xC0000199 : errorMessage = MgrUtil.getUserMessage("nt.status.nologon.workstation.trust.account");break;
		case 0xC000019a : errorMessage = MgrUtil.getUserMessage("nt.status.nologon.server.trust.account");break;
		case 0xC000019b : errorMessage = MgrUtil.getUserMessage("nt.status.domain.trust.inconsistent");break;
		case 0xC0000202 : errorMessage = MgrUtil.getUserMessage("nt.status.no.user.session.key");break;
		case 0xC0000203 : errorMessage = MgrUtil.getUserMessage("nt.status.user.session.deleted");break;
		case 0xC0000205 : errorMessage = MgrUtil.getUserMessage("nt.status.insuff.server.resources");break;
		case 0xC0000250 : errorMessage = MgrUtil.getUserMessage("nt.status.insufficient.logon.info");break;
		case 0xC0000259 : errorMessage = MgrUtil.getUserMessage("nt.status.license.quota.exceeded");break;
//		case 0xB0000008 : errorMessage = MgrUtil.getUserMessage("nt.status.ldap.sasl.wrapping.sign", MgrUtil.getUserMessage("config.radiusOnHiveAp.ldap.sasl.wrapping"));break;
		case 0xB0000008 : errorMessage = MgrUtil.getUserMessage("nt.status.ldap.sasl.wrapping.sign");break;  // revert LDAP SASL feature in FUJI, make it always return ""(plain);
		/* customize error */
		case 0xEEEEEEEE : errorMessage = MgrUtil.getUserMessage("error.retrieve.ad.domain.info.noresponse");break;
		case 0xa0000020 : errorMessage = MgrUtil.getUserMessage("error.cannot.find.specified.computerou.on.ad");break;
		/* Unknown error */
		case 0xFFFFFFFF : 
		default:
			if (msgFromAp != null && !"".equals(msgFromAp)) {
				int index = msgFromAp.indexOf(MSG_ID_SPECIFICATOR);
				if(index != -1) {
					msgFromAp = msgFromAp.substring(0, index);
				}
				errorMessage = MgrUtil.getUserMessage("bringIntoManagedListerror.unknown", msgFromAp);break;
			} else {
				errorMessage = MgrUtil.getUserMessage("error.unknown");break;
			}
		}
		return errorMessage;
	}
	
	public static void main(String[] args) {
/*		String msg = "12345234(###FFFF###)";
		int index = msg.indexOf("(###");
		msg = msg.substring(0, index);*/
	}
}
