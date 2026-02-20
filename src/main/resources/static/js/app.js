// Configuration for Medicines
const MED_ENDPOINT = '/medicines';
let medicineModal;

document.addEventListener('DOMContentLoaded', () => {
    // Check if the current page is the Medicines dashboard
    if (document.getElementById('medicineTableBody')) {
        medicineModal = new bootstrap.Modal(document.getElementById('medicineModal'));
        loadMedicines();
        document.getElementById('medicineForm').addEventListener('submit', handleMedicineSubmit);
    }
});

/** MEDICINE CRUD OPERATIONS **/

async function loadMedicines() {
    showLoading('medicineTableBody'); // Utility from common.js
    const result = await apiRequest('GET', MED_ENDPOINT); // Utility from common.js
    
    if (result.success) {
        renderMedicineTable(result.data);
    } else {
        showAlert('Error loading medicines: ' + result.error, 'danger');
    }
}

function renderMedicineTable(medicines) {
    const tbody = document.getElementById('medicineTableBody');
    if (medicines.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No medicines found.</td></tr>';
        return;
    }

    tbody.innerHTML = medicines.map(m => `
        <tr>
            <td><span class="fw-bold text-dark">${m.medId}</span></td>
            <td>${m.name}</td>
            <td>${m.companyName}</td>
            <td class="text-primary fw-bold">${formatCurrency(m.price)}</td>
            <td>${getMedicalStatusBadge(m.recordStatus)}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-primary action-btn me-1" onclick="editMedicine('${m.id}')">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger action-btn" onclick="deleteMedicine('${m.id}')">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function handleMedicineSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('id').value;
    const formData = new FormData(e.target);
    const data = formDataToJson(formData); // Utility from common.js
    
    // Explicitly handle boolean switch
    data.doctorChangeAllowed = document.getElementById('doctorChangeAllowed').checked;

    const result = id ? await update(MED_ENDPOINT, id, data) : await create(MED_ENDPOINT, data);
    
    if (result.success) {
        medicineModal.hide();
        loadMedicines();
        showAlert(id ? 'Medicine updated successfully' : 'Medicine added successfully', 'success');
    } else {
        showAlert('Save failed: ' + result.error, 'danger');
    }
}

async function editMedicine(id) {
    const result = await getById(MED_ENDPOINT, id); // Utility from common.js
    if (result.success) {
        const m = result.data;
        document.getElementById('id').value = m.id;
        document.getElementById('medId').value = m.medId;
        document.getElementById('name').value = m.name;
        document.getElementById('companyName').value = m.companyName;
        document.getElementById('price').value = m.price;
        document.getElementById('recordStatus').value = m.recordStatus;
        document.getElementById('doctorChangeAllowed').checked = m.doctorChangeAllowed;
        
        // Formats LocalDateTime string to fit datetime-local input
        if (m.expiryDate) {
            document.getElementById('expiryDate').value = m.expiryDate.substring(0, 16);
        }
        
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit me-2"></i>Edit Medicine';
        medicineModal.show();
    }
}

async function deleteMedicine(id) {
    const result = await deleteItem(MED_ENDPOINT, id); // Utility from common.js
    if (result.success) {
        loadMedicines();
        showAlert('Medicine deleted successfully', 'warning');
    }
}

function prepareMedicineForm() {
    clearForm('medicineForm'); // Utility from common.js
    document.getElementById('id').value = '';
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-plus-circle me-2"></i>Add New Medicine';
}