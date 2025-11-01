// Configure marked to use highlight.js for syntax highlighting
marked.setOptions({
    highlight: function(code, lang) {
        if (lang && hljs.getLanguage(lang)) {
            try {
                return hljs.highlight(code, { language: lang }).value;
            } catch (err) {}
        }
        try {
            return hljs.highlightAuto(code).value;
        } catch (err) {}
        return code;
    },
    breaks: true,
    gfm: true
});

// Parse and render Markdown content
function renderMarkdown() {
    document.querySelectorAll('.message-text[data-content]').forEach(function(element) {
        const content = element.getAttribute('data-content');
        if (content) {
            try {
                element.innerHTML = marked.parse(content);
            } catch (err) {
                console.error('Markdown parsing error:', err);
                element.textContent = content;
            }
        }
    });
    
    // Apply syntax highlighting to any code blocks that might have been missed
    document.querySelectorAll('pre code').forEach(function(block) {
        if (!block.classList.contains('hljs')) {
            hljs.highlightElement(block);
        }
    });
}

// Render markdown when page loads
document.addEventListener('DOMContentLoaded', function() {
    renderMarkdown();
    // Scroll to bottom after rendering
    setTimeout(scrollToBottom, 100);
});

// Also render immediately in case DOMContentLoaded already fired
if (document.readyState === 'loading') {
    // Still loading, wait for DOMContentLoaded
} else {
    // DOM is ready
    renderMarkdown();
    setTimeout(scrollToBottom, 100);
}

// Auto-scroll to bottom when window is resized (in case of dynamic content changes)
window.addEventListener('resize', function() {
    setTimeout(scrollToBottom, 100);
});

// Observe DOM changes to auto-scroll when new messages are added dynamically
if (typeof MutationObserver !== 'undefined') {
    const messagesContainer = document.querySelector('.messages-container');
    if (messagesContainer) {
        const observer = new MutationObserver(function() {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
        observer.observe(messagesContainer, {
            childList: true,
            subtree: true
        });
    }
}

// Toggle prompt creation form visibility
function togglePromptForm() {
    const form = document.getElementById('create-prompt-form');
    if (form) {
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
    }
}

// Handle chat form submission with streaming
function startStreaming() {
    const chatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('message-input');
    const sendButton = document.getElementById('send-button');
    
    if (!chatForm || !messageInput || !sendButton) {
        return;
    }

    const message = messageInput.value.trim();
    if (!message) {
        return;
    }

    // Get chat ID from form data attribute
    const chatId = chatForm.getAttribute('data-chat-id');
    if (!chatId) {
        return;
    }

    // Disable form during streaming
    messageInput.disabled = true;
    sendButton.disabled = true;
    sendButton.textContent = 'Sending...';

    // Add user message to UI immediately (saving is done by proceedResponse in OllamaService)
    addUserMessage(message);
    
    // Clear input
    messageInput.value = '';

    // Create assistant message element for streaming
    const assistantMessageEl = createAssistantMessageElement();
    const messagesContainer = document.querySelector('.messages');
    messagesContainer.appendChild(assistantMessageEl);
    
    const messageTextEl = assistantMessageEl.querySelector('.message-text');
    assistantMessageEl.classList.add('streaming');
    
    let fullResponse = '';
    let lastMessageTime = Date.now();

    const streamUrl = `/api/chats/${chatId}/stream?message=${encodeURIComponent(message)}`;
    const eventSource = new EventSource(streamUrl);

    // Helper function to scroll to bottom
    function scrollToBottom() {
        const container = document.querySelector('.messages-container');
        if (container) {
            // Use requestAnimationFrame for smooth scroll
            requestAnimationFrame(() => {
                container.scrollTop = container.scrollHeight;
            });
        }
    }

    eventSource.onmessage = function(event) {
        lastMessageTime = Date.now();
        const data = JSON.parse(event.data);
        const chunk = data.text || data.content || '';
        
        if (chunk) {
            fullResponse += chunk;
            messageTextEl.setAttribute('data-content', fullResponse);
            messageTextEl.textContent = fullResponse;
            renderMarkdown();
            scrollToBottom();
        }
    };

    eventSource.onerror = function(event) {
        eventSource.close();
    };

    eventSource.addEventListener('complete', function(event) {
        // Stream completed successfully
        assistantMessageEl.classList.remove('streaming');
        messageInput.disabled = false;
        sendButton.disabled = false;
        sendButton.textContent = 'Send';
        renderMarkdown();
        scrollToBottom();
        //        window.location.reload();
    });
}

// Initialize form handlers when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    const sendButton = document.getElementById('send-button');
    
    if (sendButton) {
        sendButton.addEventListener('click', function(e) {
            e.preventDefault();
            startStreaming();
        });
    }
});

function addUserMessage(message) {
    const messagesContainer = document.querySelector('.messages');
    if (!messagesContainer) return;

    const now = new Date();
    const timeStr = now.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });

    const messageDiv = document.createElement('div');
    messageDiv.className = 'message user';
    messageDiv.innerHTML = `
        <div class="message-header">
            <span class="message-author">user</span>
            <span class="message-time">${timeStr}</span>
        </div>
        <div class="message-text" data-content="${escapeHtml(message)}"></div>
    `;
    
    messagesContainer.appendChild(messageDiv);
    
    // Render markdown
    const messageTextEl = messageDiv.querySelector('.message-text');
    messageTextEl.innerHTML = marked.parse(message);
    
    // Scroll to bottom after adding user message
    const container = document.querySelector('.messages-container');
    if (container) {
        requestAnimationFrame(() => {
            container.scrollTop = container.scrollHeight;
        });
    }
}

function createAssistantMessageElement() {
    const now = new Date();
    const timeStr = now.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });

    const messageDiv = document.createElement('div');
    messageDiv.className = 'message assistant';
    messageDiv.innerHTML = `
        <div class="message-header">
            <span class="message-author">assistant</span>
            <span class="message-time">${timeStr}</span>
        </div>
        <div class="message-text" data-content=""></div>
    `;
    
    return messageDiv;
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

