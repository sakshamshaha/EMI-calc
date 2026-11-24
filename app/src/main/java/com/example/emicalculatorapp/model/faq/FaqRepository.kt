package com.example.emicalculatorapp.model.faq

object FaqRepository {

    val faqItems: List<FaqItem> = listOf(
        FaqItem(
            id = "faq_emi_calc",
            category = "Basics & Math",
            question = "How is my loan EMI calculated?",
            answer = "EMIverse uses the standard reducing balance compounding formula: EMI = [P × r × (1 + r)ⁿ] / [(1 + r)ⁿ - 1], where P is loan principal, r is the monthly interest rate (annual rate ÷ 12 ÷ 100), and n is the tenure in months. In the first month, interest is highest because the outstanding principal is at its maximum. Tap 'See the math' on the Calculate tab to inspect the exact formula substitution.",
            actionTag = "SEE_THE_MATH"
        ),
        FaqItem(
            id = "faq_tabs_overview",
            category = "Navigation",
            question = "What does each tab in EMIverse do?",
            answer = "• Calculate: Instant EMI calculations with donut cost chart, milestones, smart insights, and rate sensitivity.\n• Schedule: Detailed monthly/yearly amortization schedule table with PDF export.\n• Compare: Compare two loan quotes side-by-side with processing fees and winner badges.\n• Tools: Prepayment simulator, borrowing power gauge, and support resources.\n• Saved: Offline local library to bookmark, rename, and compare saved loan scenarios."
        ),
        FaqItem(
            id = "faq_prepayment",
            category = "Prepayment & Debt-Free",
            question = "How do prepayment and yearly step-up modes work?",
            answer = "Extra monthly payments reduce the principal directly, avoiding future interest compounding. You can also simulate lump-sum payments at specific milestones.\nAnnual Step-Up increases your contribution each year by a percentage: 'Extra payment mode' steps up only your extra contribution, while 'Whole EMI mode' steps up the entire monthly outflow, clearing your debt even faster."
        ),
        FaqItem(
            id = "faq_compare_loans",
            category = "Comparison",
            question = "How does loan comparison work and what do the winner badges mean?",
            answer = "Compare evaluates Loan A against Loan B including mandatory processing fees added to total outlay. We award two distinct badges:\n1. 'Lower Monthly EMI': Highlights the loan offering cheaper monthly installments, ideal for cash-flow management.\n2. 'Lower Total Outlay': Highlights the loan with the lowest total lifecycle cost (Principal + Interest + Fees)."
        ),
        FaqItem(
            id = "faq_affordability",
            category = "Affordability",
            question = "How is borrowing power calculated and what are the rule-of-thumb zones?",
            answer = "The Affordability tool uses your net monthly income and existing debt obligations to compute your Debt-to-Income (DTI) ratio across 4 financial zones:\n• Comfortable (≤ 30% DTI): Minimal financial stress.\n• Moderate (30% - 40% DTI): Well-balanced budget.\n• Stretch (40% - 50% DTI): Tight cash flow with limited emergency buffer.\n• High Risk (> 50% DTI): Danger zone with high risk of loan rejection."
        ),
        FaqItem(
            id = "faq_saved_quotes",
            category = "Saved Quotes",
            question = "Where are my saved quotes stored?",
            answer = "All saved quotes are stored locally in your device's private Room SQLite database. Quotes store your inputs (amount, rate, tenure, fee) and recalculate metrics dynamically so values are always pure. You can pin important quotes, rename them, reload them into the calculator, or compare two quotes side-by-side."
        ),
        FaqItem(
            id = "faq_pdf_export",
            category = "Reports",
            question = "How do I export my amortization schedule as a PDF?",
            answer = "Open the Schedule tab and tap 'Export PDF' in the top action bar. The app renders a clean multi-page document featuring your loan summary, Indian Rupee (₹) currency symbols, and yearly amortization table, which you can save or share via Android's native share sheet."
        ),
        FaqItem(
            id = "faq_settings",
            category = "Preferences",
            question = "What do Number Format and Effects Level settings control?",
            answer = "In Settings (gear icon):\n• Number Format: Choose 'Indian (Lakhs / Crores: ₹1,00,000)' or 'International (Millions: ₹100,000)'. This updates typing cursors, sliders, tiles, and milestones everywhere.\n• Effects Level: Choose Balanced, Dynamic, or Lite. Lite turns off blurs and continuous animations for maximum battery life and performance. System Reduced Motion is also respected."
        ),
        FaqItem(
            id = "faq_disclaimer",
            category = "Disclaimer",
            question = "Are the calculation results guaranteed?",
            answer = "All calculations, schedules, and insights in EMIverse are mathematical estimates designed for planning and educational purposes. Actual bank terms, amortization methods, rounding conventions, and fees may vary. Always consult your financial institution or certified advisor before taking a loan."
        )
    )
}
