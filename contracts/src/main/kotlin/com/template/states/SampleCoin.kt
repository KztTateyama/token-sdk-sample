package sbi.states

import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.AbstractParty
import sbi.contracts.GSCoinRecordContract

@BelongsToContract(GSCoinRecordContract::class)
data class GSCoin(
        val amount: Amount<IssuedTokenType>,
        val holder: AbstractParty,
        val tokenTypeJarHash: SecureHash?,
        val tradeNumber: String,
        val tradeDateTime: String,
        val memberShopName: String,
        val forceFlag: Boolean,
        val type: Int,
        override val participants: List<AbstractParty>
): TokenType(amount.token.tokenIdentifier, amount.token.fractionDigits), ContractState{

//    constructor(
//            amount: Amount<IssuedTokenType>,
//            holder: AbstractParty,
//            tradeNumber: String,
//            tradeDateTime: String,
//            memberShopName: String,
//            participants: List<AbstractParty>,
//            type: Int
//    ): this(amount, holder, null, tradeNumber, tradeDateTime,memberShopName, false, type, participants)

    constructor(
            amount: Amount<IssuedTokenType>,
            holder: AbstractParty,
            tradeNumber: String,
            tradeDateTime: String,
            memberShopName: String,
            forceFlag: Boolean,
            participants: List<AbstractParty>,
            type: Int
    ): this(amount, holder, null, tradeNumber, tradeDateTime,memberShopName, forceFlag, type, participants)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as GSCoin

        if (amount != other.amount) return false
        if (holder != other.holder) return false
        if (tokenTypeJarHash != other.tokenTypeJarHash) return false
        if (tradeNumber != other.tradeNumber) return false
        if (tradeDateTime != other.tradeDateTime) return false
        if (memberShopName != other.memberShopName) return false
        if (forceFlag != other.forceFlag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + holder.hashCode()
        result = 31 * result + (tokenTypeJarHash?.hashCode() ?: 0)
        result = 31 * result + tradeNumber.hashCode()
        result = 31 * result + tradeDateTime.hashCode()
        result = 31 * result + memberShopName.hashCode()
        result = 31 * result + forceFlag.hashCode()
        return result
    }


}